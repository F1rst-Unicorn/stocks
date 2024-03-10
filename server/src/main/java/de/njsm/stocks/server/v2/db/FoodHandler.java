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
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Condition;
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


    public FoodHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode setToBuyStatus(FoodForSetToBuy item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context, Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    DSL.inline(item.toBuy()),
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION,
                    FOOD.DESCRIPTION,
                    FOOD.STORE_UNIT
                    ),
                    getIdField().eq(item.id())
                            .and(getVersionField().eq(item.version()))
                            .and(FOOD.TO_BUY.ne(item.toBuy())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode setToBuyStatus(Identifiable<Food> item, boolean value) {
        return currentUpdate(Arrays.asList(
                FOOD.ID,
                FOOD.NAME,
                FOOD.VERSION.add(1),
                DSL.inline(value),
                FOOD.EXPIRATION_OFFSET,
                FOOD.LOCATION,
                FOOD.DESCRIPTION,
                FOOD.STORE_UNIT
                ),
                getIdField().eq(item.id())
                        .and(FOOD.TO_BUY.ne(value)))
                .map(this::notFoundIsOk);
    }

    public StatusCode edit(FoodForFullEditing item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            Field<?> locationField = item.location()
                    .map(v -> {
                        if (v != 0) {
                            return (Field<Integer>) DSL.inline(v);
                        } else {
                            return DSL.inline((Integer) null);
                        }
                    })
                    .orElse(FOOD.LOCATION);

            Field<?> expirationOffsetField = item.expirationOffset()
                    .map(Period::ofDays)
                    .map(v -> (Field<Period>) DSL.inline(v))
                    .orElse(FOOD.EXPIRATION_OFFSET);

            Field<?> toBuyField = item.toBuy()
                    .map(v -> (Field<Boolean>) DSL.inline(v))
                    .orElse(FOOD.TO_BUY);

            Field<?> descriptionField = item.description()
                    .map(v -> (Field<String>) DSL.inline(v))
                    .orElse(FOOD.DESCRIPTION);

            Field<?> storeUnitField = item.storeUnit()
                    .map(v -> (Field<Integer>) DSL.inline(v))
                    .orElse(FOOD.STORE_UNIT);

            Condition locationCondition = item.location()
                    .map(FOOD.LOCATION::isDistinctFrom)
                    .orElseGet(DSL::falseCondition);

            Condition expirationOffsetCondition = item.expirationOffset()
                    .map(Period::ofDays)
                    .map(FOOD.EXPIRATION_OFFSET::ne)
                    .orElseGet(DSL::falseCondition);

            Condition toBuyCondition = item.toBuy()
                    .map(FOOD.TO_BUY::ne)
                    .orElseGet(DSL::falseCondition);

            Condition descriptionCondition = item.description()
                    .map(FOOD.DESCRIPTION::ne)
                    .orElseGet(DSL::falseCondition);

            Condition storeUnitCondition = item.storeUnit()
                    .map(FOOD.STORE_UNIT::ne)
                    .orElseGet(DSL::falseCondition);

            return currentUpdate(context, Arrays.asList(
                    FOOD.ID,
                    DSL.inline(item.name()),
                    FOOD.VERSION.add(1),
                    toBuyField,
                    expirationOffsetField,
                    locationField,
                    descriptionField,
                    storeUnitField),
                    getIdField().eq(item.id())
                            .and(getVersionField().eq(item.version())
                                    .and(FOOD.NAME.ne(item.name())
                                            .or(toBuyCondition)
                                            .or(expirationOffsetCondition)
                                            .or(locationCondition)
                                            .or(descriptionCondition)
                                            .or(storeUnitCondition))
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
                FOOD.DESCRIPTION,
                FOOD.STORE_UNIT),
                FOOD.LOCATION.eq(l.id()))
                .map(this::notFoundIsOk);
    }

    public StatusCode setDescription(FoodForSetDescription item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context, Arrays.asList(
                    FOOD.ID,
                    FOOD.NAME,
                    FOOD.VERSION.add(1),
                    FOOD.TO_BUY,
                    FOOD.EXPIRATION_OFFSET,
                    FOOD.LOCATION,
                    DSL.inline(item.description()),
                    FOOD.STORE_UNIT),
                    getIdField().eq(item.id())
                            .and(getVersionField().eq(item.version()))
                            .and(FOOD.DESCRIPTION.ne(item.description())))
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
            return cursor -> BitemporalFood.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .name(cursor.getName())
                    .toBuy(cursor.getToBuy())
                    .expirationOffset(cursor.getExpirationOffset())
                    .location(cursor.getLocation())
                    .description(cursor.getDescription())
                    .storeUnit(cursor.getStoreUnit())
                    .build();
        else
            return cursor -> FoodForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .name(cursor.getName())
                    .toBuy(cursor.getToBuy())
                    .expirationOffset(cursor.getExpirationOffset())
                    .location(cursor.getLocation())
                    .description(cursor.getDescription())
                    .storeUnit(cursor.getStoreUnit())
                    .build();
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
                FOOD.DESCRIPTION,
                FOOD.STORE_UNIT
        );
    }
}
