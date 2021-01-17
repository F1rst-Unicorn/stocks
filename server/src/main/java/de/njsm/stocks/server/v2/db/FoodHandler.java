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
import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;


public class FoodHandler extends CrudDatabaseHandler<FoodRecord, Food> {


    public FoodHandler(ConnectionFactory connectionFactory,
                       String resourceIdentifier,
                       int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public StatusCode setToBuyStatus(FoodForSetToBuy item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    DSL.inline(item.isToBuy()),
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION,
                    FOOD.DESCRIPTION
                    ),
                    getIdField().eq(item.getId())
                            .and(getVersionField().eq(item.getVersion()))
                            .and(FOOD.TO_BUY.ne(item.isToBuy())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode setToBuyStatus(Identifiable<Food> item, boolean value) {
        return runCommand(context -> currentUpdate(Arrays.asList(
                FOOD.ID,
                FOOD.NAME,
                FOOD.VERSION.add(1),
                DSL.inline(value),
                FOOD.EXPIRATION_OFFSET,
                FOOD.LOCATION,
                FOOD.DESCRIPTION
                ),
                getIdField().eq(item.getId())
                        .and(FOOD.TO_BUY.ne(value)))
                .map(this::notFoundIsOk));
    }

    public StatusCode edit(FoodForEditing item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(Arrays.asList(
                    FOOD.ID,
                    DSL.inline(item.getNewName()),
                    FOOD.VERSION.add(1),
                    FOOD.TO_BUY,
                    DSL.inline(item.getExpirationOffset()),
                    DSL.inline(item.getLocation()),
                    FOOD.DESCRIPTION
                    ),
                    getIdField().eq(item.getId())
                            .and(getVersionField().eq(item.getVersion())
                                    .and(FOOD.NAME.ne(item.getNewName())
                                            .or(FOOD.EXPIRATION_OFFSET.ne(item.getExpirationOffset()))
                                            .or(FOOD.LOCATION.isDistinctFrom(item.getLocation())))
                            )
            )
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode unregisterDefaultLocation(Identifiable<Location> l) {
        return currentUpdate(Arrays.asList(
                FOOD.ID,
                FOOD.NAME,
                FOOD.VERSION.add(1),
                FOOD.TO_BUY,
                FOOD.EXPIRATION_OFFSET,
                DSL.inline((Integer) null),
                FOOD.DESCRIPTION),
                FOOD.LOCATION.eq(l.getId()))
                .map(this::notFoundIsOk);
    }

    public StatusCode setDescription(FoodForSetDescription item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    FOOD.TO_BUY,
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION,
                    DSL.inline(item.getDescription())),
                    getIdField().eq(item.getId())
                            .and(getVersionField().eq(item.getVersion()))
                            .and(FOOD.DESCRIPTION.ne(item.getDescription())))
                    .map(this::notFoundMeansInvalidVersion);
        });
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
            return cursor -> new BitemporalFood(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getName(),
                    cursor.getToBuy(),
                    cursor.getExpirationOffset(),
                    cursor.getLocation(),
                    cursor.getDescription()
            );
        else
            return cursor -> new FoodForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getName(),
                    cursor.getToBuy(),
                    cursor.getExpirationOffset(),
                    cursor.getLocation(),
                    cursor.getDescription()
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
                FOOD.LOCATION,
                FOOD.DESCRIPTION
        );
    }
}
