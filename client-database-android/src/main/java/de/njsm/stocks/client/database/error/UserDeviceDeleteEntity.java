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

package de.njsm.stocks.client.database.error;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;
import de.njsm.stocks.client.database.PreservedId;
import de.njsm.stocks.client.database.VersionFields;

@AutoValue
@Entity(tableName = "user_device_to_delete")
public abstract class UserDeviceDeleteEntity implements IdFields, VersionFields {

    @Embedded(prefix = "user_device_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId userDevice();

    public static UserDeviceDeleteEntity create(int id, int version, PreservedId userDevice) {
        return new AutoValue_UserDeviceDeleteEntity(id, version, userDevice);
    }

    @Ignore
    public static UserDeviceDeleteEntity create(int version, PreservedId userDevice) {
        return new AutoValue_UserDeviceDeleteEntity(0, version, userDevice);
    }
}
