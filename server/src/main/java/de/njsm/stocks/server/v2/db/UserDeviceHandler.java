package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class UserDeviceHandler extends CrudDatabaseHandler<UserDeviceRecord, UserDevice> {


    public UserDeviceHandler(ConnectionFactory connectionFactory,
                             String resourceIdentifier,
                             InsertVisitor<UserDeviceRecord> visitor) {
        super(connectionFactory, resourceIdentifier, visitor);
    }

    @Override
    protected Table<UserDeviceRecord> getTable() {
        return USER_DEVICE;
    }

    @Override
    protected Function<UserDeviceRecord, UserDevice> getDtoMap() {
        return record -> new UserDevice(
                record.getId().intValue(),
                record.getVersion().intValue(),
                record.getName(),
                record.getBelongsTo().intValue()
        );
    }

    @Override
    protected TableField<UserDeviceRecord, UInteger> getIdField() {
        return USER_DEVICE.ID;
    }

    @Override
    protected TableField<UserDeviceRecord, UInteger> getVersionField() {
        return USER_DEVICE.VERSION;
    }
}
