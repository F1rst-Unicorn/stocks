package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.business.data.Update;
import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.FoodItemView;
import de.njsm.stocks.client.business.data.view.FoodView;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.init.upgrade.Version;
import de.njsm.stocks.client.storage.jooq.tables.records.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.client.storage.jooq.Tables.*;
import static org.jooq.impl.DSL.condition;

public class DatabaseManager extends BaseDatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    public List<Update> getUpdates() throws DatabaseException {
        LOG.info("Getting updates");
        return runFunction(
                c -> c.selectFrom(UPDATES)
                        .fetch()
                        .map(r -> new Update(r.getTableName(),
                                             Instant.ofEpochMilli(r.getLastUpdate().getTime())
                        )
                )
        );
    }

    public void writeUpdates(List<Update> values) throws DatabaseException {
        LOG.info("Writing updates");
        runCommand(c -> {
            for (Update u : values) {
                c.update(UPDATES)
                        .set(UPDATES.LAST_UPDATE, new Timestamp(u.lastUpdate.toEpochMilli()))
                        .where(UPDATES.TABLE_NAME.eq(u.table))
                        .execute();
            }

        });
    }

    public void resetUpdates() throws DatabaseException {
        LOG.info("Resetting updates");
        runCommand(c -> c.update(UPDATES)
                .set(UPDATES.LAST_UPDATE, new Timestamp(0))
                .execute());
    }

    public List<User> getUsers() throws DatabaseException {
        LOG.info("Getting all users");
        return runFunction(c -> c.selectFrom(USER)
                .orderBy(USER.NAME)
                .fetch()
                .map(r -> new User(r.getId(), r.getVersion(), r.getName())));
    }

    public List<User> getUsers(String name) throws DatabaseException {
        LOG.info("Getting users matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(USER)
                .where(USER.NAME.eq(name))
                .fetch()
                .map(r -> new User(r.getId(), r.getVersion(), r.getName())));
    }

    public void writeUsers(List<User> values) throws DatabaseException {
        LOG.info("Writing users");
        runCommand(c -> {
            c.deleteFrom(USER).execute();
            InsertValuesStep3<UserRecord, Integer, Integer, String> base = c.insertInto(USER)
                    .columns(USER.ID, USER.VERSION, USER.NAME);

            for (User u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public void writeDevices(List<UserDevice> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(USER_DEVICE).execute();
            InsertValuesStep4<UserDeviceRecord, Integer, Integer, String, Integer> base = c.insertInto(USER_DEVICE)
                    .columns(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO);

            for (UserDevice u : values) {
                base = base.values(u.id, u.version, u.name, u.userId);
            }
            base.execute();
        });
    }

    public List<UserDeviceView> getDevices() throws DatabaseException {
        LOG.info("Getting all devices");
        return runFunction(c -> c.select(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.NAME, USER.NAME, USER.ID)
                .from(USER_DEVICE).join(USER).on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                .orderBy(USER_DEVICE.NAME)
                .fetch()
                .map(r -> new UserDeviceView(r.component1(), r.component2(), r.component3(), r.component4(), r.component5()));
    }

    public List<UserDeviceView> getDevices(String name) throws DatabaseException {
        LOG.info("Getting devices for " + name);
        return runFunction(c -> c.select(USER_DEVICE.ID, USER_DEVICE.VERSION, USER_DEVICE.NAME, USER.NAME, USER.ID))
                .from(USER_DEVICE.join(USER).on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                .where(USER_DEVICE.NAME.eq(name))
                .fetch()
                .map(r -> new UserDeviceView(r.component1(),
                        r.component2(),
                        r.component3(),
                        r.component4(),
                        r.component5()));
    }

    public List<Location> getLocations() throws DatabaseException {
        LOG.info("Getting all locations");
        return runFunction(c -> c.selectFrom(LOCATION)
                .orderBy(LOCATION.NAME)
                .fetch()
                .map(r -> new Location(r.getId(), r.getVersion(), r.getName())));
    }

    public List<Location> getLocations(String name) throws DatabaseException {
        LOG.info("Getting locations matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(LOCATION)
                .where(LOCATION.NAME.eq(name))
                .fetch()
                .map(r -> new Location(r.getId(), r.getVersion(), r.getName())));
    }


    public List<Location> getLocationsForFoodType(int foodId) throws DatabaseException {
        LOG.info("Getting locations for food type " + foodId);
        return runFunction(c -> c.selectDistinct(LOCATION.ID, LOCATION.NAME, LOCATION.VERSION)
                .from(LOCATION)
                .join(FOOD_ITEM).on(FOOD_ITEM.STORED_IN.eq(LOCATION.ID))
                .where(FOOD_ITEM.OF_TYPE.eq(foodId))
                .fetch()
                .map(r -> new Location(r.component1(), r.component3(), r.component2())));
    }

    public void writeLocations(List<Location> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(LOCATION).execute();
            InsertValuesStep3<LocationRecord, Integer, Integer, String> base = c.insertInto(LOCATION)
                    .columns(LOCATION.ID, LOCATION.VERSION, LOCATION.NAME);

            for (Location u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public void writeFood(List<Food> values) throws DatabaseException {
        runCommand(c -> {
            c.deleteFrom(FOOD).execute();
            InsertValuesStep3<FoodRecord, Integer, Integer, String> base = c.insertInto(FOOD)
                    .columns(FOOD.ID, FOOD.VERSION, FOOD.NAME);

            for (Food u : values) {
                base = base.values(u.id, u.version, u.name);
            }
            base.execute();
        });
    }

    public List<Food> getFood(String name) throws DatabaseException {
        LOG.info("Getting food matching name '" + name + "'");
        return runFunction(c -> c.selectFrom(FOOD)
                .where(FOOD.NAME.eq(name)))
                .fetch()
                .map(r -> new Food(r.getId(), r.getVersion(), r.getName()));
    }

    public List<Food> getFood() throws DatabaseException {
        LOG.info("Getting all food");
        return runFunction(c -> c.selectFrom(FOOD)
                .orderBy(FOOD.ID)
                .fetch()
                .map(r -> new Food(r.getId(), r.getVersion(), r.getName())));
    }

    public List<FoodItem> getItems(int foodId) throws DatabaseException {
        LOG.info("Getting food items of type " + foodId);
        return runFunction(c -> c.selectFrom(FOOD_ITEM)
                .where(FOOD_ITEM.OF_TYPE.eq(foodId))
                .fetch()
                .map(r -> new FoodItem(
                        r.getId(),
                        r.getVersion(),
                        Instant.ofEpochMilli(r.getEatBy().getTime()),
                        r.getOfType(),
                        r.getStoredIn(),
                        r.getRegisters(),
                        r.getBuys()
                )));
    }

    public List<FoodView> getItems(String user, String location) throws DatabaseException {
        LOG.info("Getting items matching user '" + user + "' and location '" + location + "'");
        return runFunction(c -> {
            de.njsm.stocks.client.storage.jooq.tables.Food F = FOOD;
            de.njsm.stocks.client.storage.jooq.tables.FoodItem I = FOOD_ITEM;
            de.njsm.stocks.client.storage.jooq.tables.Location L = LOCATION;

            Table<Record7<Integer, Timestamp, Integer, Integer, String, String, String>> richFoodItems =
                    c.select(I.OF_TYPE.as("type"), I.EAT_BY.as("date"), I.STORED_IN.as("stored_in"), I.BUYS.as("buys"), L.NAME.as("location"), USER.NAME.as("user"), USER_DEVICE.NAME.as("device"))
                            .from(FOOD_ITEM, LOCATION, USER, USER_DEVICE)
                            .where(L.ID.eq(I.STORED_IN))
                            .and(USER.ID.eq(I.BUYS))
                            .and(USER_DEVICE.ID.eq(I.REGISTERS))
                            .asTable("richFoodItems");

            Condition userCondition = condition("{0} = {1}", "", user).or(richFoodItems.field("buys", Integer.class).in(c.select(USER.ID).from(USER).where(USER.NAME.eq(user))));
            Condition locationCondition = condition("{0} = {1}", "", location).or(richFoodItems.field("stored_in", Integer.class).in(c.select(L.ID).from(L).where(L.NAME.eq(location))));

            List<FoodView> result = new ArrayList<>();
            int lastId = -1;
            FoodView f = null;

            Result<Record7<Integer, String, Integer, Timestamp, String, String, String>> items = c.select(F.ID, F.NAME, F.VERSION,
                    richFoodItems.field("date", Timestamp.class),
                    richFoodItems.field("location", String.class),
                    richFoodItems.field("user", String.class),
                    richFoodItems.field("device", String.class))
                    .from(F)
                    .leftOuterJoin(richFoodItems)
                    .on(F.ID.eq(richFoodItems.field("type", Integer.class)))
                    .where(userCondition.and(locationCondition))
                    .orderBy(F.ID.asc(), richFoodItems.field("date").asc())
                    .fetch();

            for (Record7<Integer, String, Integer, Timestamp, String, String, String> rs : items) {
                int id = rs.component1();
                if (id != lastId) {
                    if (f != null) {
                        result.add(f);
                    }
                    Food newFood = new Food(id, rs.component3(), rs.component2());
                    f = new FoodView(newFood);
                }

                Timestamp date = rs.component4();
                if (date != null) {     // may have no elements due to outer join
                    FoodItemView item = new FoodItemView();
                    item.eatByDate = Instant.ofEpochMilli(date.getTime());
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
            c.deleteFrom(FOOD_ITEM).execute();
            InsertValuesStep7<FoodItemRecord, Integer, Integer, Timestamp, Integer, Integer, Integer, Integer> base = c.insertInto(FOOD_ITEM)
                    .columns(FOOD_ITEM.ID, FOOD_ITEM.VERSION, FOOD_ITEM.EAT_BY, FOOD_ITEM.STORED_IN, FOOD_ITEM.BUYS, FOOD_ITEM.REGISTERS, FOOD_ITEM.OF_TYPE);

            for (FoodItem u : values) {
                base = base.values(u.id,
                        u.version,
                        new Timestamp(u.eatByDate.toEpochMilli()),
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
        Optional<FoodItem> item = runFunction(c -> c.selectFrom(FOOD_ITEM)
                .where(FOOD_ITEM.OF_TYPE.eq(foodId))
                .orderBy(FOOD_ITEM.EAT_BY.asc())
                .limit(1)
                .fetchOptional()
                .map(r -> new FoodItem(
                        r.getId(),
                        r.getVersion(),
                        Instant.ofEpochMilli(r.getEatBy().getTime()),
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
                return c.selectFrom(CONFIG)
                        .where(CONFIG.KEY.eq("db.version"))
                        .fetchOne(r -> Version.create(r.getValue()));
            } else
                return Version.PRE_VERSIONED;
        });
    }
}
