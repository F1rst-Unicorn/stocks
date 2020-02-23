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
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class CrudDatabaseHandler<T extends UpdatableRecord<T>, R extends VersionedData>
        extends FailSafeDatabaseHandler
        implements PresenceChecker<R> {

    protected InsertVisitor<T> visitor;

    public CrudDatabaseHandler(ConnectionFactory connectionFactory,
                               String resourceIdentifier,
                               int timeout,
                               InsertVisitor<T> visitor) {
        super(connectionFactory, resourceIdentifier, timeout);
        this.visitor = visitor;
    }

    public Validation<StatusCode, Integer> add(R item) {
        return runFunction(context -> {
            int lastInsertId = visitor.visit(item, context.insertInto(getTable()))
                    .returning(getIdField())
                    .fetch()
                    .getValue(0, getIdField());
            return Validation.success(lastInsertId);
        });

    }

    public Validation<StatusCode, Stream<R>> get() {
        return runFunction(context -> {
            Stream<R> result = context
                            .selectFrom(getTable())
                            .fetchSize(1024)
                            .stream()
                            .map(getDtoMap());

            return Validation.success(result);
        });
    }

    public StatusCode delete(R item) {
        return runCommand(context -> {
            if (isMissing(item, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.deleteFrom(getTable())
                    .where(getIdField().eq(item.id)
                            .and(getVersionField().eq(item.version)))
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.INVALID_DATA_VERSION;
        });
    }

    @Override
    public boolean isMissing(R item, DSLContext context) {
        int count = context.selectCount()
                .from(getTable())
                .where(getIdField().eq(item.id))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }

    protected abstract Table<T> getTable();

    protected abstract Function<T, R> getDtoMap();

    protected abstract TableField<T, Integer> getIdField();

    protected abstract TableField<T, Integer> getVersionField();
}
