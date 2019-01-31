package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import org.jooq.Table;
import org.jooq.TableField;

import java.sql.Connection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class UserDeviceHandler extends CrudDatabaseHandler<UserDeviceRecord, UserDevice> {


    public UserDeviceHandler(Connection connection,
                             String resourceIdentifier,
                             InsertVisitor<UserDeviceRecord> visitor) {
        super(connection, resourceIdentifier, visitor);
    }

    public Validation<StatusCode, List<UserDevice>> getDevicesOfUser(User user) {
        return runFunction(context -> {
            List<UserDevice> result = context
                    .selectFrom(USER_DEVICE)
                    .where(USER_DEVICE.BELONGS_TO.eq(user.id))
                    .fetch()
                    .stream()
                    .map(getDtoMap())
                    .collect(Collectors.toList());

            return Validation.success(result);

        });
    }

    @Override
    protected Table<UserDeviceRecord> getTable() {
        return USER_DEVICE;
    }

    @Override
    protected Function<UserDeviceRecord, UserDevice> getDtoMap() {
        return record -> new UserDevice(
                record.getId(),
                record.getVersion(),
                record.getName(),
                record.getBelongsTo()
        );
    }

    @Override
    protected TableField<UserDeviceRecord, Integer> getIdField() {
        return USER_DEVICE.ID;
    }

    @Override
    protected TableField<UserDeviceRecord, Integer> getVersionField() {
        return USER_DEVICE.VERSION;
    }
}
