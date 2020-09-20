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
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
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
                           String resourceIdentifier,
                           int timeout,
                           InsertVisitor<FoodItemRecord> visitor,
                           PresenceChecker<UserDevice> userDeviceChecker,
                           PresenceChecker<User> userChecker) {
        super(connectionFactory, resourceIdentifier, timeout, visitor);
        this.userDeviceChecker = userDeviceChecker;
        this.userChecker = userChecker;
    }

    public StatusCode edit(FoodItem item) {
        return runCommand(context -> {
            if (isCurrentlyMissing(item, context)) {
                return StatusCode.NOT_FOUND;
            }

            OffsetDateTime newEatByDate = item.eatByDate.atOffset(ZoneOffset.UTC);

            return currentUpdate(Arrays.asList(
                    FOOD_ITEM.ID,
                    DSL.inline(OffsetDateTime.from(newEatByDate)),
                    FOOD_ITEM.OF_TYPE,
                    DSL.inline(item.storedIn),
                    FOOD_ITEM.REGISTERS,
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.VERSION.add(1)
                    ),
                    FOOD_ITEM.ID.eq(item.id)
                            .and(FOOD_ITEM.VERSION.eq(item.version))
                            .and(FOOD_ITEM.EAT_BY.ne(newEatByDate)
                                    .or(FOOD_ITEM.STORED_IN.ne(item.storedIn))
                            )
            )
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode transferFoodItems(UserDevice from, UserDevice to) {
        return runCommand(context -> {
            if (userDeviceChecker.isCurrentlyMissing(from, context)) {
                LOG.warn("Origin ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            if (userDeviceChecker.isCurrentlyMissing(to, context)) {
                LOG.warn("Target ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }
            return currentUpdate(Arrays.asList(
                    FOOD_ITEM.ID,
                    FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.STORED_IN,
                    DSL.inline(to.id),
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.VERSION.add(1)
                    ),
                    FOOD_ITEM.REGISTERS.eq(from.id))
                    .map(this::notFoundIsOk);
        });
    }

    public StatusCode transferFoodItems(User from, User to, List<UserDevice> fromDevices, UserDevice toDevice) {
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
                    .map(UserDevice::getId)
                    .collect(Collectors.toList());

            return currentUpdate(Arrays.asList(
                    FOOD_ITEM.ID,
                    FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.STORED_IN,
                    DSL.inline(toDevice.id),
                    DSL.inline(to.id),
                    FOOD_ITEM.VERSION.add(1)
                    ),
                    FOOD_ITEM.BUYS.eq(from.id)
                            .and(FOOD_ITEM.REGISTERS.in(deviceIds)))
                    .map(this::notFoundIsOk);
        });
    }

    public StatusCode deleteItemsOfType(Food item) {
        return runCommand(context -> currentDelete(FOOD_ITEM.OF_TYPE.eq(item.id))
                .map(this::notFoundIsOk));
    }

    public StatusCode deleteItemsStoredIn(Location location) {
        return runCommand(context -> currentDelete(FOOD_ITEM.STORED_IN.eq(location.id))
                 .map(this::notFoundIsOk));
    }

    boolean areItemsStoredIn(Location location, DSLContext context) {
        Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

        int result = context.select(DSL.count())
                .from(FOOD_ITEM)
                .where(FOOD_ITEM.STORED_IN.eq(location.id)
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
            return cursor -> new FoodItem(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getEatBy().toInstant(),
                    cursor.getOfType(),
                    cursor.getStoredIn(),
                    cursor.getRegisters(),
                    cursor.getBuys(),
                    cursor.getCreatorUser(),
                    cursor.getCreatorUserDevice()
            );
        else
            return cursor -> new FoodItem(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getEatBy().toInstant(),
                    cursor.getOfType(),
                    cursor.getStoredIn(),
                    cursor.getRegisters(),
                    cursor.getBuys()
            );
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
                FOOD_ITEM.VERSION
        );
    }
}
