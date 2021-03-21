/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.*;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.postgresql.PGStatement;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class CrudDatabaseHandler<T extends TableRecord<T>, N extends Entity<N>>
        extends FailSafeDatabaseHandler
        implements PresenceChecker<N> {

    private static final Logger LOG = LogManager.getLogger(CrudDatabaseHandler.class);

    public static final OffsetDateTime INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_POSITIVE_INFINITY), ZoneId.of("UTC"));

    public static final OffsetDateTime NEGATIVE_INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_NEGATIVE_INFINITY), ZoneId.of("UTC"));

    public CrudDatabaseHandler(ConnectionFactory connectionFactory,
                               String resourceIdentifier,
                               int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
    }

    public Validation<StatusCode, Integer> add(Insertable<T, N> item) {
        return runFunction(context -> {
            int lastInsertId = item.insertValue(context.insertInto(getTable()), principals)
                    .returning(getIdField())
                    .fetch()
                    .getValue(0, getIdField());
            return Validation.success(lastInsertId);
        });
    }

    /**
     * CF 10.23
     */
    public Validation<StatusCode, Stream<N>> get(boolean bitemporal, Instant startingFrom) {
        return runFunction(context -> {

            Condition bitemporalSelector;
            if (bitemporal)
                bitemporalSelector = DSL.trueCondition();
            else
                bitemporalSelector = nowAsBestKnown();

            OffsetDateTime startingFromWithOffset = OffsetDateTime.from(startingFrom.atOffset(ZoneOffset.UTC));
            bitemporalSelector = bitemporalSelector.and(getTransactionTimeStartField().greaterThan(startingFromWithOffset)
                    .or(getTransactionTimeEndField().greaterThan(startingFromWithOffset)
                            .and(getTransactionTimeEndField().lessThan(INFINITY))));

            Stream<N> result = context
                    .selectFrom(getTable())
                    .where(bitemporalSelector)
                    .fetchSize(1024)
                    .stream()
                    .map(getDtoMap(bitemporal));

            return Validation.success(result);
        });
    }

    public StatusCode delete(Versionable<N> item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentDelete(getIdField().eq(item.getId())
                    .and(getVersionField().eq(item.getVersion())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    /**
     * CF 10.11
     */
    StatusCode currentDelete(Condition condition) {
        return runCommand(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            List<Field<?>> fields = getNontemporalFields();
            List<Field<?>> fieldsWithTime = getFieldsWithTimeAndCreator(fields);

            int changedItems = context.insertInto(getTable())
                    .columns(fieldsWithTime)
                    .select(
                            context.select(fields)
                                    .select(getValidTimeStartField(),
                                            now,
                                            now,
                                            DSL.inline(CrudDatabaseHandler.INFINITY),
                                            DSL.inline(principals.getDid()))
                                    .from(getTable())
                                    .where(condition
                                            .and(getValidTimeStartField().lessThan(now))
                                            .and(getValidTimeEndField().greaterThan(now))
                                            .and(getTransactionTimeEndField().eq(CrudDatabaseHandler.INFINITY))))
                    .execute();

            context.update(getTable())
                    .set(getTransactionTimeEndField(), now)
                    .where(condition
                            .and(getValidTimeEndField().greaterThan(now))
                            .and(getTransactionTimeEndField().eq(CrudDatabaseHandler.INFINITY)))
                    .execute();

            if (0 < changedItems)
                return StatusCode.SUCCESS;
            else
                return StatusCode.NOT_FOUND;

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
        List<Field<?>> fields = getNontemporalFields();
        List<Field<?>> fieldsWithTime = getFieldsWithTimeAndCreator(fields);

        int changedItems = context.insertInto(getTable())
                .columns(fieldsWithTime)
                .select(
                        context.select(valuesToUpdate)
                                .select(
                                        now,
                                        getValidTimeEndField(),
                                        now,
                                        DSL.inline(CrudDatabaseHandler.INFINITY),
                                        DSL.inline(principals.getDid()))
                                .from(getTable())
                                .where(condition
                                        .and(getValidTimeStartField().lessOrEqual(now))
                                        .and(getValidTimeEndField().greaterThan(now))
                                        .and(getTransactionTimeEndField().eq(INFINITY))
                                ))
                .execute();

        context.insertInto(getTable())
                .columns(fieldsWithTime)
                .select(
                        context.select(fields)
                                .select(
                                        getValidTimeStartField(),
                                        now,
                                        now,
                                        DSL.inline(INFINITY),
                                        DSL.inline(principals.getDid())
                                ).from(getTable())
                                .where(condition
                                        .and(getValidTimeStartField().lessThan(now))
                                        .and(getValidTimeEndField().greaterThan(now))
                                        .and(getTransactionTimeEndField().eq(INFINITY))
                                )
                )
                .execute();

        context.update(getTable())
                .set(getTransactionTimeEndField(), now)
                .where(condition
                        .and(getValidTimeStartField().lessThan(now))
                        .and(getValidTimeEndField().greaterThan(now))
                        .and(getTransactionTimeEndField().eq(INFINITY))
                )
                .execute();

        context.insertInto(getTable())
                .columns(fieldsWithTime)
                .select(
                        context.select(valuesToUpdate)
                                .select(
                                        getValidTimeStartField(),
                                        getValidTimeEndField(),
                                        now,
                                        DSL.inline(CrudDatabaseHandler.INFINITY),
                                        DSL.inline(principals.getDid()))
                                .from(getTable())
                                .where(condition
                                        .and(getValidTimeStartField().greaterThan(now))
                                        .and(getTransactionTimeEndField().eq(INFINITY))
                                )
                ).execute();

        context.update(getTable())
                .set(getTransactionTimeEndField(), now)
                .where(condition
                        .and(getValidTimeStartField().greaterThan(now))
                        .and(getTransactionTimeEndField().eq(INFINITY))
                        .and(getTransactionTimeStartField().lt(now))
                )
                .execute();

        if (changedItems > 0)
            return StatusCode.SUCCESS;
        else
            return StatusCode.NOT_FOUND;
    }

    @Override
    public boolean isCurrentlyMissing(Identifiable<N> item, DSLContext context) {
        int count = context.selectCount()
                .from(getTable())
                .where(getIdField().eq(item.getId()).and(nowAsBestKnown()))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }

    public StatusCode cleanDataOlderThan(Period period) {
        return runCommand(context -> {

            OffsetDateTime oldestDateToPreserve = OffsetDateTime.now().minus(period);

            int count = context.deleteFrom(getTable())
                    .where(getTransactionTimeEndField().lessThan(oldestDateToPreserve))
                    .execute();

            if (count > 0) {
                LOG.info("Cleaned up {} rows from {}", count, this);
            }

            return StatusCode.SUCCESS;
        });
    }

    private List<Field<?>> getFieldsWithTimeAndCreator(List<Field<?>> fields) {
        List<Field<?>> fieldsWithTime = new ArrayList<>(fields);
        fieldsWithTime.add(getValidTimeStartField());
        fieldsWithTime.add(getValidTimeEndField());
        fieldsWithTime.add(getTransactionTimeStartField());
        fieldsWithTime.add(getTransactionTimeEndField());
        fieldsWithTime.add(getInitiatesField());
        return fieldsWithTime;
    }

    protected abstract Table<T> getTable();

    protected abstract Function<T, N> getDtoMap(boolean bitemporal);

    protected abstract TableField<T, Integer> getIdField();

    protected abstract TableField<T, Integer> getVersionField();

    protected abstract List<Field<?>> getNontemporalFields();

    protected Field<OffsetDateTime> getValidTimeStartField() {
        return getTable().field("valid_time_start", OffsetDateTime.class);
    }

    protected Field<OffsetDateTime> getValidTimeEndField() {
        return getTable().field("valid_time_end", OffsetDateTime.class);
    }

    protected Field<OffsetDateTime> getTransactionTimeStartField() {
        return getTable().field("transaction_time_start", OffsetDateTime.class);
    }

    protected Field<OffsetDateTime> getTransactionTimeEndField() {
        return getTable().field("transaction_time_end", OffsetDateTime.class);
    }

    protected Field<Integer> getInitiatesField() {
        return getTable().field("initiates", Integer.class);
    }

    protected Condition nowAsBestKnown() {
        return getValidTimeStartField().lessOrEqual(DSL.currentOffsetDateTime())
                .and(DSL.currentOffsetDateTime().lessThan(getValidTimeEndField()))
                .and(getTransactionTimeEndField().eq(INFINITY));
    }

    protected StatusCode notFoundMeansInvalidVersion(StatusCode code) {
        if (code == StatusCode.NOT_FOUND)
            return StatusCode.INVALID_DATA_VERSION;
        else
            return code;
    }

    protected StatusCode notFoundIsOk(StatusCode code) {
        if (code == StatusCode.NOT_FOUND)
            return StatusCode.SUCCESS;
        else
            return code;
    }

    @Override
    public String toString() {
        return getTable().getQualifiedName().toString();
    }
}
