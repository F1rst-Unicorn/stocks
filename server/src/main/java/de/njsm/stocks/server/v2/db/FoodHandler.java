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
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;


public class FoodHandler extends CrudDatabaseHandler<FoodRecord, Food> {


    public FoodHandler(ConnectionFactory connectionFactory,
                       String resourceIdentifier,
                       int timeout,
                       InsertVisitor<FoodRecord> visitor) {
        super(connectionFactory, resourceIdentifier, timeout, visitor);
    }

    public StatusCode setToBuyStatus(Food item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            StatusCode result = currentUpdate(Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    DSL.inline(item.toBuy),
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION
                    ),
                    getIdField().eq(item.id)
                            .and(getVersionField().eq(item.version))
                            .and(FOOD.TO_BUY.ne(item.toBuy)));

            return notFoundMeansInvalidVersion(result);
        });
    }

    public StatusCode setToBuyStatus(Food item, boolean value) {
        return runCommand(context -> {
            StatusCode result = currentUpdate(Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    DSL.inline(value),
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION
                    ),
                    getIdField().eq(item.id)
                            .and(FOOD.TO_BUY.ne(value)));

            return notFoundIsOk(result);
        });
    }

    public StatusCode edit(Food item, String newName, Period expirationOffset, Integer location) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            StatusCode result = currentUpdate(Arrays.asList(
                    FOOD.ID,
                    DSL.inline(newName),
                    FOOD.VERSION.add(1),
                    FOOD.TO_BUY,
                    DSL.inline(expirationOffset),
                    DSL.inline(location)
                    ),
                    getIdField().eq(item.id)
                            .and(getVersionField().eq(item.version)
                                    .and(FOOD.NAME.ne(newName)
                                            .or(FOOD.EXPIRATION_OFFSET.ne(expirationOffset))
                                            .or(FOOD.LOCATION.ne(location)))
                            )
            );

            return notFoundMeansInvalidVersion(result);
        });
    }

    public StatusCode unregisterDefaultLocation(Location l) {
        StatusCode result = currentUpdate(Arrays.asList(
                FOOD.ID,
                FOOD.NAME,
                FOOD.VERSION.add(1),
                FOOD.TO_BUY,
                FOOD.EXPIRATION_OFFSET,
                DSL.inline((Integer) null)),
                FOOD.LOCATION.eq(l.id));

        if (result == StatusCode.NOT_FOUND) {
            return StatusCode.SUCCESS;
        }
        return result;
    }

    @Override
    protected Table<FoodRecord> getTable() {
        return FOOD;
    }

    @Override
    protected TableField<FoodRecord, Integer> getIdField() {
        return FOOD.ID;
    }

    @Override
    protected TableField<FoodRecord, Integer> getVersionField() {
        return FOOD.VERSION;
    }

    @Override
    protected Function<FoodRecord, Food> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> new Food(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getName(),
                    cursor.getToBuy(),
                    cursor.getExpirationOffset(),
                    cursor.getLocation()
            );
        else
            return cursor -> new Food(
                    cursor.getId(),
                    cursor.getName(),
                    cursor.getVersion(),
                    cursor.getToBuy(),
                    cursor.getExpirationOffset(),
                    cursor.getLocation()
            );
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                FOOD.ID,
                FOOD.NAME,
                FOOD.VERSION,
                FOOD.TO_BUY,
                FOOD.EXPIRATION_OFFSET,
                FOOD.LOCATION
        );
    }
}
