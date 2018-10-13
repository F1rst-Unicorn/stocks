package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.sql.Timestamp;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD_ITEM;


public class FoodItemHandler extends CrudDatabaseHandler<FoodItemRecord, FoodItem> {

    private static final Logger LOG = LogManager.getLogger(FoodItemHandler.class);

    private PresenceChecker<UserDevice> userDeviceChecker;

    public FoodItemHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           InsertVisitor<FoodItemRecord> visitor,
                           PresenceChecker<UserDevice> userDeviceChecker) {
        super(connectionFactory, resourceIdentifier, visitor);
        this.userDeviceChecker = userDeviceChecker;
    }

    public StatusCode edit(FoodItem item) {
        return runCommand(context -> {
            if (isMissing(item, context)) {
                return StatusCode.NOT_FOUND;
            }

            int changedItems = context.update(FOOD_ITEM)
                    .set(FOOD_ITEM.EAT_BY, new Timestamp(item.eatByDate.toEpochMilli()))
                    .set(FOOD_ITEM.STORED_IN, UInteger.valueOf(item.storedIn))
                    .set(FOOD_ITEM.VERSION, FOOD_ITEM.VERSION.add(1))
                    .where(FOOD_ITEM.ID.eq(UInteger.valueOf(item.id))
                            .and(FOOD_ITEM.VERSION.eq(UInteger.valueOf(item.version))))
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
                    .set(FOOD_ITEM.REGISTERS, UInteger.valueOf(to.id))
                    .set(FOOD_ITEM.VERSION, FOOD_ITEM.VERSION.add(1))
                    .where(FOOD_ITEM.REGISTERS.eq(UInteger.valueOf(from.id)))
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    @Override
    protected Table<FoodItemRecord> getTable() {
        return FOOD_ITEM;
    }

    @Override
    protected TableField<FoodItemRecord, UInteger> getIdField() {
        return FOOD_ITEM.ID;
    }

    @Override
    protected TableField<FoodItemRecord, UInteger> getVersionField() {
        return FOOD_ITEM.VERSION;
    }

    @Override
    protected Function<FoodItemRecord, FoodItem> getDtoMap() {
        return cursor -> new FoodItem(
                cursor.getId().intValue(),
                cursor.getVersion().intValue(),
                cursor.getEatBy().toInstant(),
                cursor.getOfType().intValue(),
                cursor.getStoredIn().intValue(),
                cursor.getRegisters().intValue(),
                cursor.getBuys().intValue()
        );
    }

}
