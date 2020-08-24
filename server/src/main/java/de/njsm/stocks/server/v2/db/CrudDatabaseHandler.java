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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.VersionedData;
import fj.data.Validation;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.postgresql.PGStatement;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class CrudDatabaseHandler<T extends TableRecord<T>, R extends VersionedData>
        extends FailSafeDatabaseHandler
        implements PresenceChecker<R> {

    public static final OffsetDateTime INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_POSITIVE_INFINITY), ZoneId.of("UTC"));

    public static final OffsetDateTime NEGATIVE_INFINITY = OffsetDateTime.ofInstant(Instant.ofEpochMilli(PGStatement.DATE_NEGATIVE_INFINITY), ZoneId.of("UTC"));

    protected InsertVisitor<T> visitor;

    public CrudDatabaseHandler(ConnectionFactory connectionFactory,
                               String resourceIdentifier,
                               int timeout,
                               InsertVisitor<T> visitor) {
        super(connectionFactory, resourceIdentifier, timeout);
        this.visitor = visitor;
    }

    /**
     * CF 10.4
     */
    public Validation<StatusCode, Integer> add(R item) {
        return runFunction(context -> {
            int lastInsertId = visitor.visit(item, context.insertInto(getTable()))
                    .returning(getIdField())
                    .fetch()
                    .getValue(0, getIdField());
            return Validation.success(lastInsertId);
        });
    }

    /**
     * CF 10.23
     */
    public Validation<StatusCode, Stream<R>> get(boolean bitemporal) {
        return runFunction(context -> {

            Condition bitemporalSelector;
            if (bitemporal)
                bitemporalSelector = DSL.trueCondition();
            else
                bitemporalSelector = nowAsBestKnown();

            Stream<R> result = context
                    .selectFrom(getTable())
                    .where(bitemporalSelector)
                    .fetchSize(1024)
                    .stream()
                    .map(getDtoMap());

            return Validation.success(result);
        });
    }

    /**
     * CF 10.11
     */
    public StatusCode delete(R item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentDelete(getIdField().eq(item.id)
                    .and(getVersionField().eq(item.version)));
        });
    }

    /**
     * CF 10.11
     */
    public StatusCode currentDelete(Condition condition) {
        return runCommand(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            List<Field<?>> fields = getNontemporalFields();
            List<Field<?>> fieldsWithTime = getFieldsWithTime(fields);

            int changedItems = context.insertInto(getTable())
                    .columns(fieldsWithTime)
                    .select(
                            context.select(fields)
                                    .select(getValidTimeStartField(),
                                            now,
                                            now,
                                            DSL.inline(CrudDatabaseHandler.INFINITY))
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
                return StatusCode.INVALID_DATA_VERSION;

        });
    }

    /**
     * CF 10.7
     */
    public StatusCode currentUpdate(List<Field<?>> valuesToUpdate, Condition condition) {
        return runCommand(context -> {

            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            List<Field<?>> fields = getNontemporalFields();
            List<Field<?>> fieldsWithTime = getFieldsWithTime(fields);

            int changedItems = context.insertInto(getTable())
                    .columns(fieldsWithTime)
                    .select(
                            context.select(valuesToUpdate)
                                    .select(
                                            now,
                                            getValidTimeEndField(),
                                            now,
                                            DSL.inline(CrudDatabaseHandler.INFINITY))
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
                                            DSL.inline(INFINITY)
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
                                            DSL.inline(CrudDatabaseHandler.INFINITY))
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
                    )
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.NOT_FOUND;
        });
    }

    @Override
    public boolean isCurrentlyMissing(R item, DSLContext context) {
        int count = context.selectCount()
                .from(getTable())
                .where(getIdField().eq(item.id).and(nowAsBestKnown()))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }

    protected List<Field<?>> getFieldsWithTime(List<Field<?>> fields) {
        List<Field<?>> fieldsWithTime = new ArrayList<>(fields);
        fieldsWithTime.add(getValidTimeStartField());
        fieldsWithTime.add(getValidTimeEndField());
        fieldsWithTime.add(getTransactionTimeStartField());
        fieldsWithTime.add(getTransactionTimeEndField());
        return fieldsWithTime;
    }

    protected abstract Table<T> getTable();

    protected abstract Function<T, R> getDtoMap();

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
}
