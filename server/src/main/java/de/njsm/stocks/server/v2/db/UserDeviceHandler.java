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
