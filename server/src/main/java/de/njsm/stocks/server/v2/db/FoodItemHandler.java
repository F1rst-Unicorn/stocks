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

import de.njsm.stocks.common.api.User;
import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.BitemporalFoodItem;
import de.njsm.stocks.common.api.FoodItemForEditing;
import de.njsm.stocks.common.api.FoodItemForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD_ITEM;


public class FoodItemHandler extends CrudDatabaseHandler<FoodItemRecord, FoodItem> {

    private static final Logger LOG = LogManager.getLogger(FoodItemHandler.class);

    private final PresenceChecker<UserDevice> userDeviceChecker;

    private final PresenceChecker<User> userChecker;

    public FoodItemHandler(ConnectionFactory connectionFactory,
                           PresenceChecker<UserDevice> userDeviceChecker,
                           PresenceChecker<User> userChecker) {
        super(connectionFactory);
        this.userDeviceChecker = userDeviceChecker;
        this.userChecker = userChecker;
    }

    public StatusCode edit(FoodItemForEditing item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context)) {
                return StatusCode.NOT_FOUND;
            }

            OffsetDateTime newEatByDate = item.eatBy().atOffset(ZoneOffset.UTC);

            Field<?> unitField = item.unit()
                    .map(v -> (Field<Integer>) DSL.inline(v))
                    .orElse(FOOD_ITEM.UNIT);

            Condition unitCondition = item.unit()
                    .map(FOOD_ITEM.UNIT::ne)
                    .orElseGet(DSL::falseCondition);

            return currentUpdate(context, Arrays.asList(
                    FOOD_ITEM.ID,
                    DSL.inline(OffsetDateTime.from(newEatByDate)),
                    FOOD_ITEM.OF_TYPE,
                    DSL.inline(item.storedIn()),
                    FOOD_ITEM.REGISTERS,
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.VERSION.add(1),
                    unitField
                    ),
                    FOOD_ITEM.ID.eq(item.id())
                            .and(FOOD_ITEM.VERSION.eq(item.version()))
                            .and(FOOD_ITEM.EAT_BY.ne(newEatByDate)
                                    .or(FOOD_ITEM.STORED_IN.ne(item.storedIn()))
                                    .or(unitCondition)
                            )
            )
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode transferFoodItems(Identifiable<UserDevice> from, Identifiable<UserDevice> to) {
        return runCommand(context -> {
            if (userDeviceChecker.isCurrentlyMissing(from, context)) {
                LOG.warn("Origin ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            if (userDeviceChecker.isCurrentlyMissing(to, context)) {
                LOG.warn("Target ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }
            return currentUpdate(context, Arrays.asList(
                    FOOD_ITEM.ID,
                    FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.STORED_IN,
                    DSL.inline(to.id()),
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.VERSION.add(1),
                    FOOD_ITEM.UNIT
                    ),
                    FOOD_ITEM.REGISTERS.eq(from.id()))
                    .map(this::notFoundIsOk);
        });
    }

    public StatusCode transferFoodItems(Identifiable<User> from,
                                        Identifiable<User> to,
                                        List<Identifiable<UserDevice>> fromDevices,
                                        Identifiable<UserDevice> toDevice) {
        return runCommand(context -> {
            if (userChecker.isCurrentlyMissing(from, context)) {
                LOG.warn("Origin ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            if (userChecker.isCurrentlyMissing(to, context)) {
                LOG.warn("Target ID " + to + " not found");
                return StatusCode.NOT_FOUND;
            }

            List<Integer> deviceIds = fromDevices.stream()
                    .map(Identifiable::id)
                    .collect(Collectors.toList());

            return currentUpdate(context, Arrays.asList(
                    FOOD_ITEM.ID,
                    FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.STORED_IN,
                    DSL.inline(toDevice.id()),
                    DSL.inline(to.id()),
                    FOOD_ITEM.VERSION.add(1),
                    FOOD_ITEM.UNIT
                    ),
                    FOOD_ITEM.BUYS.eq(from.id())
                            .and(FOOD_ITEM.REGISTERS.in(deviceIds)))
                    .map(this::notFoundIsOk);
        });
    }

    public StatusCode deleteItemsOfType(Identifiable<Food> item) {
        return runCommand(context -> currentDelete(FOOD_ITEM.OF_TYPE.eq(item.id()))
                .map(this::notFoundIsOk));
    }

    public StatusCode deleteItemsStoredIn(Identifiable<Location> location) {
        return runCommand(context -> currentDelete(FOOD_ITEM.STORED_IN.eq(location.id()))
                 .map(this::notFoundIsOk));
    }

    boolean areItemsStoredIn(Versionable<Location> location, DSLContext context) {
        Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

        int result = context.select(DSL.count())
                .from(FOOD_ITEM)
                .where(FOOD_ITEM.STORED_IN.eq(location.id())
                        .and(getValidTimeStartField().lessOrEqual(now))
                        .and(now.lessThan(getValidTimeEndField()))
                        .and(getTransactionTimeEndField().eq(INFINITY))
                )
                .fetchOne(0, int.class);

        return result != 0;
    }

    @Override
    protected Table<FoodItemRecord> getTable() {
        return FOOD_ITEM;
    }

    @Override
    protected TableField<FoodItemRecord, Integer> getIdField() {
        return FOOD_ITEM.ID;
    }

    @Override
    protected TableField<FoodItemRecord, Integer> getVersionField() {
        return FOOD_ITEM.VERSION;
    }

    @Override
    protected Function<FoodItemRecord, FoodItem> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalFoodItem.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .eatByDate(cursor.getEatBy().toInstant())
                    .ofType(cursor.getOfType())
                    .storedIn(cursor.getStoredIn())
                    .registers(cursor.getRegisters())
                    .buys(cursor.getBuys())
                    .unit(cursor.getUnit())
                    .build();
        else
            return cursor -> FoodItemForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .eatByDate(cursor.getEatBy().toInstant())
                    .ofType(cursor.getOfType())
                    .storedIn(cursor.getStoredIn())
                    .registers(cursor.getRegisters())
                    .buys(cursor.getBuys())
                    .unit(cursor.getUnit())
                    .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                FOOD_ITEM.ID,
                FOOD_ITEM.EAT_BY,
                FOOD_ITEM.OF_TYPE,
                FOOD_ITEM.STORED_IN,
                FOOD_ITEM.REGISTERS,
                FOOD_ITEM.BUYS,
                FOOD_ITEM.VERSION,
                FOOD_ITEM.UNIT
        );
    }
}
