/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.visitor.JooqInsertionVisitor;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.postgresql.PGStatement;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.common.api.StatusCode.*;
import static org.jooq.impl.DSL.*;

public abstract class CrudDatabaseHandler<T extends TableRecord<T>, N extends Entity<N>>
        extends FailSafeDatabaseHandler
        implements PresenceChecker<N> {

    private static final Logger LOG = LogManager.getLogger(CrudDatabaseHandler.class);

    public static final OffsetDateTime INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_POSITIVE_INFINITY), ZoneId.of("UTC"));

    public static final OffsetDateTime NEGATIVE_INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_NEGATIVE_INFINITY), ZoneId.of("UTC"));

    public CrudDatabaseHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode add(Insertable<N> item) {
        return addReturningId(item).toEither().left().orValue(StatusCode.SUCCESS);
    }

    public Validation<StatusCode, Integer> addReturningId(Insertable<N> item) {
        return runFunction(context -> {
            int lastInsertId = new JooqInsertionVisitor<T>()
                    .visit(item, new JooqInsertionVisitor.Input<>(context.insertInto(tableDescription().table()), getPrincipals()))
                    .returning(tableDescription().id())
                    .fetch()
                    .getValue(0, tableDescription().id());
            return Validation.success(lastInsertId);
        });
    }

    /**
     * CF 10.23
     */
    public Validation<StatusCode, List<N>> get(Instant startingFrom, Instant upUntil) {
        return runFunction(context -> {

            OffsetDateTime startingFromWithOffset = startingFrom.atOffset(ZoneOffset.UTC);
            OffsetDateTime upUntilWithOffset = upUntil.atOffset(ZoneOffset.UTC);
            var greaterThanStartingFrom = tableDescription().transactionTimeStart().greaterThan(startingFromWithOffset)
                    .or(tableDescription().transactionTimeEnd().greaterThan(startingFromWithOffset)
                            .and(tableDescription().transactionTimeEnd().lessThan(INFINITY)));
            var lessThanUpUntil = tableDescription().transactionTimeStart().lessOrEqual(upUntilWithOffset)
                    .or(tableDescription().transactionTimeEnd().lessOrEqual(upUntilWithOffset)
                            .and(tableDescription().transactionTimeEnd().lessThan(INFINITY)));

            List<N> result = context
                    .selectFrom(tableDescription().table())
                    .where(greaterThanStartingFrom
                            .and(lessThanUpUntil))
                    .fetch(getDtoMap());

            return Validation.success(result);
        });
    }

    <R extends TableRecord<R>> Optional<Record> prolongValidTimeStart(DSLContext context, TableDescription<R> table, OffsetDateTime validTimeStart, int id, SelectFieldOrAsterisk... fields) {
        Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
        var inner = table.table().as("inner");

        // insert entry with extended valid_time_start
        var insertedRecord = context.insertInto(table.table())
                .columns(table.getAllFields())
                .select(
                        context.select(table.getNontemporalFields())
                                .select(
                                        table.id(),
                                        table.version(),
                                        least(table.validTimeStart(), val(validTimeStart)),
                                        table.validTimeEnd(),
                                        now,
                                        inline(CrudDatabaseHandler.INFINITY),
                                        inline(getPrincipals().getDid()))
                                .from(table.table())
                                .where(
                                        table.id().eq(id)
                                        .and(table.validTimeStart().eq(select(min(inner.field(table.validTimeStart())))
                                                        .from(inner)
                                                        .where(inner.field(table.transactionTimeEnd()).eq(INFINITY))
                                                        .and(inner.field(table.id()).eq(id))))
                                        .and(table.validTimeStart().gt(validTimeStart))
                                        .and(table.validTimeEnd().eq(INFINITY))
                                ))
                .returningResult(fields)
                .fetchOptional();

        // terminate older entry if new one was inserted
        if (insertedRecord.isPresent()) {
            context.update(table.table())
                      .set(table.transactionTimeEnd(), now)
                      .where(
                              table.id().eq(id)
                                      .and(table.transactionTimeEnd().eq(INFINITY))
                                      .and(table.transactionTimeStart().lt(now))
                      )
                      .execute();
          }
        return insertedRecord;
    }

    public StatusCode delete(Versionable<N> item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return NOT_FOUND;

            return currentDelete(tableDescription().id().eq(item.id())
                    .and(tableDescription().version().eq(item.version())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    /**
     * CF 10.11
     */
    StatusCode currentDelete(Condition condition) {
        return runCommand(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

            int changedItems = context.insertInto(tableDescription().table())
                    .columns(tableDescription().getAllFields())
                    .select(
                            context.select(tableDescription().getNontemporalFields())
                                    .select(
                                            tableDescription().id(),
                                            tableDescription().version(),
                                            tableDescription().validTimeStart(),
                                            now,
                                            now,
                                            DSL.inline(CrudDatabaseHandler.INFINITY),
                                            DSL.inline(getPrincipals().getDid()))
                                    .from(tableDescription().table())
                                    .where(condition
                                            .and(tableDescription().validTimeStart().lessThan(now))
                                            .and(tableDescription().validTimeEnd().greaterThan(now))
                                            .and(tableDescription().transactionTimeEnd().eq(CrudDatabaseHandler.INFINITY))))
                    .execute();

            context.update(tableDescription().table())
                    .set(tableDescription().transactionTimeEnd(), now)
                    .where(condition
                            .and(tableDescription().validTimeEnd().greaterThan(now))
                            .and(tableDescription().transactionTimeEnd().eq(CrudDatabaseHandler.INFINITY)))
                    .execute();

            if (0 < changedItems)
                return StatusCode.SUCCESS;
            else
                return NOT_FOUND;

        });
    }

    StatusCode currentUpdate(List<Field<?>> valuesToUpdate, Condition condition) {
        return runCommand(context -> currentUpdate(context, valuesToUpdate, condition));
    }

    /**
     * CF 10.7
     */
    StatusCode currentUpdate(DSLContext context, List<Field<?>> valuesToUpdate, Condition condition) {
        Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

        // insert updated values starting from now
        int changedItems = context.insertInto(tableDescription().table())
                .columns(tableDescription().getAllFields())
                .select(
                        context.select(valuesToUpdate)
                                .select(
                                        tableDescription().id(),
                                        tableDescription().version().add(1),
                                        now,
                                        tableDescription().validTimeEnd(),
                                        now,
                                        DSL.inline(CrudDatabaseHandler.INFINITY),
                                        DSL.inline(getPrincipals().getDid()))
                                .from(tableDescription().table())
                                .where(condition
                                        .and(tableDescription().validTimeStart().lessOrEqual(now))
                                        .and(tableDescription().validTimeEnd().greaterThan(now))
                                        .and(tableDescription().transactionTimeEnd().eq(INFINITY))
                                ))
                .execute();

        // insert unchanged values up to now
        context.insertInto(tableDescription().table())
                .columns(tableDescription().getAllFields())
                .select(
                        context.select(tableDescription().getNontemporalFields())
                                .select(
                                        tableDescription().id(),
                                        tableDescription().version(),
                                        tableDescription().validTimeStart(),
                                        now,
                                        now,
                                        DSL.inline(INFINITY),
                                        DSL.inline(getPrincipals().getDid())
                                ).from(tableDescription().table())
                                .where(condition
                                        .and(tableDescription().validTimeStart().lessThan(now))
                                        .and(tableDescription().validTimeEnd().greaterThan(now))
                                        .and(tableDescription().transactionTimeEnd().eq(INFINITY))
                                )
                )
                .execute();

        // terminate former entry before now
        context.update(tableDescription().table())
                .set(tableDescription().transactionTimeEnd(), now)
                .where(condition
                        .and(tableDescription().validTimeStart().lessThan(now))
                        .and(tableDescription().validTimeEnd().greaterThan(now))
                        .and(tableDescription().transactionTimeEnd().eq(INFINITY))
                )
                .execute();

        // insert updated values starting from now
        context.insertInto(tableDescription().table())
                .columns(tableDescription().getAllFields())
                .select(
                        context.select(valuesToUpdate)
                                .select(
                                        tableDescription().id(),
                                        tableDescription().version().add(1),
                                        tableDescription().validTimeStart(),
                                        tableDescription().validTimeEnd(),
                                        now,
                                        DSL.inline(CrudDatabaseHandler.INFINITY),
                                        DSL.inline(getPrincipals().getDid()))
                                .from(tableDescription().table())
                                .where(condition
                                        .and(tableDescription().validTimeStart().greaterThan(now))
                                        .and(tableDescription().transactionTimeEnd().eq(INFINITY))
                                )
                ).execute();

        // terminate former entries starting from now
        context.update(tableDescription().table())
                .set(tableDescription().transactionTimeEnd(), now)
                .where(condition
                        .and(tableDescription().validTimeStart().greaterThan(now))
                        .and(tableDescription().transactionTimeEnd().eq(INFINITY))
                        .and(tableDescription().transactionTimeStart().lt(now))
                )
                .execute();

        if (changedItems > 0)
            return StatusCode.SUCCESS;
        else
            return NOT_FOUND;
    }

    @Override
    public boolean isCurrentlyMissing(Identifiable<N> item, DSLContext context) {
        int count = context.selectCount()
                .from(tableDescription().table())
                .where(tableDescription().id().eq(item.id()).and(nowAsBestKnown()))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }

    public StatusCode checkPresenceInThisVersion(Versionable<N> item, DSLContext context) {
        Optional<Record1<Integer>> dbVersionable = context.select(tableDescription().version())
                .from(tableDescription().table())
                .where(tableDescription().id().eq(item.id()).and(nowAsBestKnown()))
                .fetchOptional();

        return dbVersionable.map(v -> {
                    if (v.value1() == item.version())
                        return SUCCESS;
                    else
                        return INVALID_DATA_VERSION;
                }
        ).orElse(NOT_FOUND);
    }

    public StatusCode cleanDataOlderThan(Period period) {
        return runCommand(context -> {

            OffsetDateTime oldestDateToPreserve = OffsetDateTime.now().minus(period);

            int count = context.deleteFrom(tableDescription().table())
                    .where(tableDescription().transactionTimeEnd().lessThan(oldestDateToPreserve))
                    .execute();

            if (count > 0) {
                LOG.info("Cleaned up {} rows from {}", count, this);
            }

            return StatusCode.SUCCESS;
        });
    }

    private List<Field<?>> getFieldsWithTimeAndCreator(List<TableField<T, ?>> fields) {
        List<Field<?>> fieldsWithTime = new ArrayList<>(fields);
        fieldsWithTime.add(tableDescription().validTimeStart());
        fieldsWithTime.add(tableDescription().validTimeEnd());
        fieldsWithTime.add(tableDescription().transactionTimeStart());
        fieldsWithTime.add(tableDescription().transactionTimeEnd());
        fieldsWithTime.add(tableDescription().initiates());
        return fieldsWithTime;
    }

    protected abstract RecordMapper<T, N> getDtoMap();

    abstract TableDescription<T> tableDescription();

    protected Condition nowAsBestKnown() {
        return tableDescription().validTimeStart().lessOrEqual(DSL.currentOffsetDateTime())
                .and(DSL.currentOffsetDateTime().lessThan(tableDescription().validTimeEnd()))
                .and(tableDescription().transactionTimeEnd().eq(INFINITY));
    }

    protected StatusCode notFoundMeansInvalidVersion(StatusCode code) {
        if (code == NOT_FOUND)
            return StatusCode.INVALID_DATA_VERSION;
        else
            return code;
    }

    protected StatusCode notFoundIsOk(StatusCode code) {
        if (code == NOT_FOUND)
            return StatusCode.SUCCESS;
        else
            return code;
    }

    @Override
    public String toString() {
        return tableDescription().table().getQualifiedName().toString();
    }

    protected Principals getPrincipals() {
        return (Principals) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
