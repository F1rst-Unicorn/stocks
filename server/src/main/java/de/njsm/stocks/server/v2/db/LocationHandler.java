package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;


public class LocationHandler extends CrudRenameDatabaseHandler<LocationRecord, Location> {


    public LocationHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           InsertVisitor<LocationRecord> visitor) {
        super(connectionFactory, resourceIdentifier, visitor);
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
