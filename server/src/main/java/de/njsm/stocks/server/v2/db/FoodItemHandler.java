package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD_ITEM;


public class FoodItemHandler extends CrudDatabaseHandler<FoodItemRecord, FoodItem> {

    private static final Logger LOG = LogManager.getLogger(FoodItemHandler.class);

    private PresenceChecker<UserDevice> userDeviceChecker;

    private PresenceChecker<User> userChecker;

    public FoodItemHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           InsertVisitor<FoodItemRecord> visitor,
                           PresenceChecker<UserDevice> userDeviceChecker,
                           PresenceChecker<User> userChecker) {
        super(connectionFactory, resourceIdentifier, visitor);
        this.userDeviceChecker = userDeviceChecker;
        this.userChecker = userChecker;
    }

    public StatusCode edit(FoodItem item) {
        return runCommand(context -> {
            if (isMissing(item, context)) {
                return StatusCode.NOT_FOUND;
            }

            int changedItems = context.update(FOOD_ITEM)
                    .set(FOOD_ITEM.EAT_BY, OffsetDateTime.from(item.eatByDate.atOffset(ZoneOffset.UTC)))
                    .set(FOOD_ITEM.STORED_IN, item.storedIn)
                    .set(FOOD_ITEM.VERSION, FOOD_ITEM.VERSION.add(1))
                    .where(FOOD_ITEM.ID.eq(item.id)
                            .and(FOOD_ITEM.VERSION.eq(item.version)))
                    .execute();

            if (changedItems == 1) {
                return StatusCode.SUCCESS;
            } else {
                return StatusCode.INVALID_DATA_VERSION;
            }
        });
    }

    public StatusCode transferFoodItems(UserDevice from, UserDevice to) {
        return runCommand(context -> {
            if (userDeviceChecker.isMissing(from, context)) {
                LOG.warn("Origin ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            if (userDeviceChecker.isMissing(to, context)) {
                LOG.warn("Target ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            context.update(FOOD_ITEM)
                    .set(FOOD_ITEM.REGISTERS, to.id)
                    .set(FOOD_ITEM.VERSION, FOOD_ITEM.VERSION.add(1))
                    .where(FOOD_ITEM.REGISTERS.eq(from.id))
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    public StatusCode transferFoodItems(User from, User to) {
        return runCommand(context -> {
            if (userChecker.isMissing(from, context)) {
                LOG.warn("Origin ID " + from + " not found");
                return StatusCode.NOT_FOUND;
            }

            if (userChecker.isMissing(to, context)) {
                LOG.warn("Target ID " + to + " not found");
                return StatusCode.NOT_FOUND;
            }

            context.update(FOOD_ITEM)
                    .set(FOOD_ITEM.BUYS, to.id)
                    .set(FOOD_ITEM.VERSION, FOOD_ITEM.VERSION.add(1))
                    .where(FOOD_ITEM.BUYS.eq(from.id))
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    public StatusCode deleteItemsStoredIn(Location location) {
        return runCommand(context -> {

            context.deleteFrom(FOOD_ITEM)
                    .where(FOOD_ITEM.STORED_IN.eq(location.id))
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    boolean areItemsStoredIn(Location location, DSLContext context) {
        int result = context.select(DSL.count())
                    .from(FOOD_ITEM)
                    .where(FOOD_ITEM.STORED_IN.eq(location.id))
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
    protected Function<FoodItemRecord, FoodItem> getDtoMap() {
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

}
