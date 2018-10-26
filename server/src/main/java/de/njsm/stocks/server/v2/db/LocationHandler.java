package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;


public class LocationHandler extends CrudRenameDatabaseHandler<LocationRecord, Location> {

    private FoodItemHandler foodItemHandler;

    public LocationHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           InsertVisitor<LocationRecord> visitor,
                           FoodItemHandler foodItemHandler) {
        super(connectionFactory, resourceIdentifier, visitor);
        this.foodItemHandler = foodItemHandler;
    }

    @Override
    public StatusCode delete(Location item) {
        return runCommand(context -> {
            StatusCode checkResult = performDeleteChecks(item, context);

            if (checkResult != StatusCode.SUCCESS)
                return checkResult;

            return deleteInternally(item, context);
        });
    }

    private StatusCode performDeleteChecks(Location location, DSLContext context) {

        if (isMissing(location, context))
            return StatusCode.NOT_FOUND;

        Boolean locationContainsItems = foodItemHandler.areItemsStoredIn(location, context);
        if (locationContainsItems)
            return StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION;
        else
            return StatusCode.SUCCESS;
    }

    private StatusCode deleteInternally(Location item, DSLContext context) {
        int changedItems = context.deleteFrom(getTable())
                .where(getIdField().eq(UInteger.valueOf(item.id))
                        .and(getVersionField().eq(UInteger.valueOf(item.version))))
                .execute();

        if (changedItems == 1)
            return StatusCode.SUCCESS;
        else
            return StatusCode.INVALID_DATA_VERSION;
    }

    @Override
    protected Table<LocationRecord> getTable() {
        return LOCATION;
    }

    @Override
    protected TableField<LocationRecord, UInteger> getIdField() {
        return LOCATION.ID;
    }

    @Override
    protected TableField<LocationRecord, UInteger> getVersionField() {
        return LOCATION.VERSION;
    }

    @Override
    protected TableField<LocationRecord, String> getNameColumn() {
        return LOCATION.NAME;
    }

    @Override
    protected Function<LocationRecord, Location> getDtoMap() {
        return cursor -> new Location(
                cursor.getId().intValue(),
                cursor.getName(),
                cursor.getVersion().intValue()
        );
    }

}
