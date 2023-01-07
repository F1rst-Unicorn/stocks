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

package de.njsm.stocks.clientold.storage;

import de.njsm.stocks.clientold.business.data.User;
import de.njsm.stocks.clientold.business.data.*;
import de.njsm.stocks.clientold.business.data.view.FoodItemView;
import de.njsm.stocks.clientold.business.data.view.FoodView;
import de.njsm.stocks.clientold.business.data.view.UserDeviceView;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import de.njsm.stocks.clientold.exceptions.InputException;
import de.njsm.stocks.clientold.init.upgrade.Version;
import de.njsm.stocks.clientold.storage.jooq.tables.records.*;
import de.njsm.stocks.clientold.business.data.*;
import de.njsm.stocks.clientold.business.json.InstantDeserialiser;
import de.njsm.stocks.clientold.business.json.InstantSerialiser;
import de.njsm.stocks.clientold.storage.jooq.Tables;
import de.njsm.stocks.clientold.storage.jooq.tables.records.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.condition;

public class DatabaseManager extends BaseDatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    public List<de.njsm.stocks.clientold.business.data.Update> getUpdates() throws DatabaseException {
        LOG.info("Getting updates");
        return runFunction(
                c -> c.selectFrom(Tables.UPDATES)
                        .fetch()
                        .map(r -> new de.njsm.stocks.clientold.business.data.Update(r.getTableName(),
                                        InstantDeserialiser.parseTimestamp(r.getLastUpdate())
                                )
                        )
        );
    }

    public void writeUpdates(List<de.njsm.stocks.clientold.business.data.Update> values) throws DatabaseException {
        LOG.info("Writing updates");
        runCommand(c -> {
            for (de.njsm.stocks.clientold.business.data.Update u : values) {
                c.update(Tables.UPDATES)
                        .set(Tables.UPDATES.LAST_UPDATE, InstantSerialiser.FORMAT.format(u.lastUpdate))
                        .where(Tables.UPDATES.TABLE_NAME.eq(u.table))
                        .execute();
            }

        });
    }

    public void resetUpdates() throws DatabaseException {
        LOG.info("Resetting updates");
        runCommand(c -> c.update(Tables.UPDATES)
                .set(Tables.UPDATES.LAST_UPDATE, InstantSerialiser.FORMAT.format(Instant.EPOCH))
                .execute());
    }

    public List<de.njsm.stocks.clientold.business.data.User> getUsers() throws DatabaseException {
        LOG.info("Getting all users");
        return runFunction(c -> c.selectFrom(Tables.USER)
                .orderBy(Tables.USER.NAME)
                .fetch()
                .map(r -> new de.njsm.stocks.clientold.business.data.User(r.getId(), r.getVersion(), r.getName())));
    }

    public List<de.njsm.stocks.clientold.business.data.User> getUsers(String name) throws DatabaseException {
        LOG.info("Getting users matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(Tables.USER)
                .where(Tables.USER.NAME.eq(name))
                .fetch()
                .map(r -> new de.njsm.stocks.clientold.business.data.User(r.getId(), r.getVersion(), r.getName())));
    }

    public void writeUsers(List<de.njsm.stocks.clientold.business.data.User> values) throws DatabaseException {
        LOG.info("Writing users");
        runCommand(c -> {
            c.deleteFrom(Tables.USER).execute();
            InsertValuesStep3<UserRecord, Integer, Integer, String> base = c.insertInto(Tables.USER)
                    .columns(Tables.USER.ID, Tables.USER.VERSION, Tables.USER.NAME);

            for (User u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public void writeDevices(List<UserDevice> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(Tables.USER_DEVICE).execute();
            InsertValuesStep4<UserDeviceRecord, Integer, Integer, String, Integer> base = c.insertInto(Tables.USER_DEVICE)
                    .columns(Tables.USER_DEVICE.ID, Tables.USER_DEVICE.VERSION, Tables.USER_DEVICE.NAME, Tables.USER_DEVICE.BELONGS_TO);

            for (UserDevice u : values) {
                base = base.values(u.id, u.version, u.name, u.userId);
            }
            base.execute();
        });
    }

    public List<UserDeviceView> getDevices() throws DatabaseException {
        LOG.info("Getting all devices");
        return runFunction(c -> c.select(Tables.USER_DEVICE.ID, Tables.USER_DEVICE.VERSION, Tables.USER_DEVICE.NAME, Tables.USER.NAME, Tables.USER.ID)
                .from(Tables.USER_DEVICE).join(Tables.USER).on(Tables.USER.ID.eq(Tables.USER_DEVICE.BELONGS_TO)))
                .orderBy(Tables.USER_DEVICE.NAME)
                .fetch()
                .map(r -> new UserDeviceView(r.component1(), r.component2(), r.component3(), r.component4(), r.component5()));
    }

    public List<UserDeviceView> getDevices(String name) throws DatabaseException {
        LOG.info("Getting devices for " + name);
        return runFunction(c -> c.select(Tables.USER_DEVICE.ID, Tables.USER_DEVICE.VERSION, Tables.USER_DEVICE.NAME, Tables.USER.NAME, Tables.USER.ID))
                .from(Tables.USER_DEVICE.join(Tables.USER).on(Tables.USER.ID.eq(Tables.USER_DEVICE.BELONGS_TO)))
                .where(Tables.USER_DEVICE.NAME.eq(name))
                .fetch()
                .map(r -> new UserDeviceView(r.component1(),
                        r.component2(),
                        r.component3(),
                        r.component4(),
                        r.component5()));
    }

    public List<Location> getLocations() throws DatabaseException {
        LOG.info("Getting all locations");
        return runFunction(c -> c.selectFrom(Tables.LOCATION)
                .orderBy(Tables.LOCATION.NAME)
                .fetch()
                .map(r -> new Location(r.getId(), r.getVersion(), r.getName())));
    }

    public List<Location> getLocations(String name) throws DatabaseException {
        LOG.info("Getting locations matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(Tables.LOCATION)
                .where(Tables.LOCATION.NAME.eq(name))
                .fetch()
                .map(r -> new Location(r.getId(), r.getVersion(), r.getName())));
    }


    public List<Location> getLocationsForFoodType(int foodId) throws DatabaseException {
        LOG.info("Getting locations for food type " + foodId);
        return runFunction(c -> c.selectDistinct(Tables.LOCATION.ID, Tables.LOCATION.NAME, Tables.LOCATION.VERSION)
                .from(Tables.LOCATION)
                .join(Tables.FOOD_ITEM).on(Tables.FOOD_ITEM.STORED_IN.eq(Tables.LOCATION.ID))
                .where(Tables.FOOD_ITEM.OF_TYPE.eq(foodId))
                .fetch()
                .map(r -> new Location(r.component1(), r.component3(), r.component2())));
    }

    public void writeLocations(List<Location> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(Tables.LOCATION).execute();
            InsertValuesStep3<LocationRecord, Integer, Integer, String> base = c.insertInto(Tables.LOCATION)
                    .columns(Tables.LOCATION.ID, Tables.LOCATION.VERSION, Tables.LOCATION.NAME);

            for (Location u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public void writeFood(List<Food> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(Tables.FOOD).execute();
            InsertValuesStep3<FoodRecord, Integer, Integer, String> base = c.insertInto(Tables.FOOD)
                    .columns(Tables.FOOD.ID, Tables.FOOD.VERSION, Tables.FOOD.NAME);

            for (Food u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public List<Food> getFood(String name) throws DatabaseException {
        LOG.info("Getting food matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(Tables.FOOD)
                .where(Tables.FOOD.NAME.eq(name)))
                .fetch()
                .map(r -> new Food(r.getId(), r.getVersion(), r.getName()));
    }

    public List<Food> getFood() throws DatabaseException {
        LOG.info("Getting all food");
        return runFunction(c -> c.selectFrom(Tables.FOOD)
                .orderBy(Tables.FOOD.ID)
                .fetch()
                .map(r -> new Food(r.getId(), r.getVersion(), r.getName())));
    }

    public List<FoodItem> getItems(int foodId) throws DatabaseException {
        LOG.info("Getting food items of type " + foodId);
        return runFunction(c -> c.selectFrom(Tables.FOOD_ITEM)
                .where(Tables.FOOD_ITEM.OF_TYPE.eq(foodId))
                .fetch()
                .map(r -> new FoodItem(
                        r.getId(),
                        r.getVersion(),
                        InstantDeserialiser.parseTimestamp(r.getEatBy()),
                        r.getOfType(),
                        r.getStoredIn(),
                        r.getRegisters(),
                        r.getBuys()
                )));
    }

    public List<FoodView> getItems(String user, String location) throws DatabaseException {
        LOG.info("Getting items matching user '" + user + "' and location '" + location + "'");
        return runFunction(c -> {
            de.njsm.stocks.clientold.storage.jooq.tables.Food F = Tables.FOOD;
            de.njsm.stocks.clientold.storage.jooq.tables.FoodItem I = Tables.FOOD_ITEM;
            de.njsm.stocks.clientold.storage.jooq.tables.Location L = Tables.LOCATION;

            Table<Record7<Integer, String, Integer, Integer, String, String, String>> richFoodItems =
                    c.select(I.OF_TYPE.as("type"), I.EAT_BY.as("date"), I.STORED_IN.as("stored_in"), I.BUYS.as("buys"), L.NAME.as("location"), Tables.USER.NAME.as("user"), Tables.USER_DEVICE.NAME.as("device"))
                            .from(Tables.FOOD_ITEM, Tables.LOCATION, Tables.USER, Tables.USER_DEVICE)
                            .where(L.ID.eq(I.STORED_IN))
                            .and(Tables.USER.ID.eq(I.BUYS))
                            .and(Tables.USER_DEVICE.ID.eq(I.REGISTERS))
                            .asTable("richFoodItems");

            Condition userCondition = condition("{0} = {1}", "", user).or(richFoodItems.field("buys", Integer.class).in(c.select(Tables.USER.ID).from(Tables.USER).where(Tables.USER.NAME.eq(user))));
            Condition locationCondition = condition("{0} = {1}", "", location).or(richFoodItems.field("stored_in", Integer.class).in(c.select(L.ID).from(L).where(L.NAME.eq(location))));

            List<FoodView> result = new ArrayList<>();
            int lastId = -1;
            FoodView f = null;

            Result<Record7<Integer, String, Integer, String, String, String, String>> items = c.select(F.ID, F.NAME, F.VERSION,
                    richFoodItems.field("date", String.class),
                    richFoodItems.field("location", String.class),
                    richFoodItems.field("user", String.class),
                    richFoodItems.field("device", String.class))
                    .from(F)
                    .leftOuterJoin(richFoodItems)
                    .on(F.ID.eq(richFoodItems.field("type", Integer.class)))
                    .where(userCondition.and(locationCondition))
                    .orderBy(F.ID.asc(), richFoodItems.field("date").asc())
                    .fetch();

            for (Record7<Integer, String, Integer, String, String, String, String> rs : items) {
                int id = rs.component1();
                if (id != lastId) {
                    if (f != null) {
                        result.add(f);
                    }
                    Food newFood = new Food(id, rs.component3(), rs.component2());
                    f = new FoodView(newFood);
                }

                String date = rs.component4();
                if (date != null) {     // may have no elements due to outer join
                    FoodItemView item = new FoodItemView();
                    item.eatByDate = InstantDeserialiser.parseTimestamp(rs.component4());
                    item.location = rs.component5();
                    item.user = rs.component6();
                    item.device = rs.component7();
                    if (f != null) {
                        f.add(item);
                    }
                }
                lastId = id;
            }

            if (f != null) {
                result.add(f);
            }
            return result;
        });
    }

    public void writeFoodItems(List<FoodItem> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(Tables.FOOD_ITEM).execute();
            InsertValuesStep7<FoodItemRecord, Integer, Integer, String, Integer, Integer, Integer, Integer> base = c.insertInto(Tables.FOOD_ITEM)
                    .columns(Tables.FOOD_ITEM.ID, Tables.FOOD_ITEM.VERSION, Tables.FOOD_ITEM.EAT_BY, Tables.FOOD_ITEM.STORED_IN, Tables.FOOD_ITEM.BUYS, Tables.FOOD_ITEM.REGISTERS, Tables.FOOD_ITEM.OF_TYPE);

            for (FoodItem u : values) {
                base = base.values(u.id,
                        u.version,
                        InstantSerialiser.FORMAT.format(u.eatByDate),
                        u.storedIn,
                        u.buys,
                        u.registers,
                        u.ofType);
            }
            base.execute();
        });

    }

    public FoodItem getNextItem(int foodId) throws InputException, DatabaseException {
        LOG.info("Getting next item for food id " + foodId);
        Optional<FoodItem> item = runFunction(c -> c.selectFrom(Tables.FOOD_ITEM)
                .where(Tables.FOOD_ITEM.OF_TYPE.eq(foodId))
                .orderBy(Tables.FOOD_ITEM.EAT_BY.asc())
                .limit(1)
                .fetchOptional()
                .map(r -> new FoodItem(
                        r.getId(),
                        r.getVersion(),
                        InstantDeserialiser.parseTimestamp(r.getEatBy()),
                        r.getOfType(),
                        r.getStoredIn(),
                        r.getRegisters(),
                        r.getBuys())));

        if (item.isPresent())
            return item.get();
        else
            throw new InputException("You don't have any...");
    }

    public Version getDbVersion() throws DatabaseException {
        LOG.info("Getting version");
        return runFunction(c -> {
            boolean tableExists = c.select()
                    .from("sqlite_master")
                    .where("type = {0}", "table")
                    .and("name = {0}", "Config")
                    .fetchOptional()
                    .isPresent();

            if (tableExists) {
                return c.selectFrom(Tables.CONFIG)
                        .where(Tables.CONFIG.KEY.eq("db.version"))
                        .fetchOne(r -> Version.create(r.getValue()));
            } else
                return Version.PRE_VERSIONED;
        });
    }
}
