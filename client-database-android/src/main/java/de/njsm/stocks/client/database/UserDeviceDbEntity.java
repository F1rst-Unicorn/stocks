/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General private License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 *
 * You should have received a copy of the GNU General private License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.client.database;

import androidx.room.Entity;
import androidx.room.Index;
import com.google.auto.value.AutoValue;

import java.time.Instant;

@Entity(tableName = "user_device", primaryKeys = {"id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"id", "valid_time_start", "valid_time_end"}, name = "user_device_current"),
                @Index(value = {"id"}, name = "user_device_pkey"),
                @Index(value = {"transaction_time_start"}, name = "user_device_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "user_device_transaction_time_end"),
        })
@AutoValue
public abstract class UserDeviceDbEntity extends ServerDbEntity<UserDeviceDbEntity> implements UserDeviceFields {

    static Builder builder() {
        return new AutoValue_UserDeviceDbEntity.Builder();
    }

    public static UserDeviceDbEntity create(int id,
                                            int version,
                                            Instant validTimeStart,
                                            Instant validTimeEnd,
                                            Instant transactionTimeStart,
                                            Instant transactionTimeEnd,
                                            int initiates,
                                            String name,
                                            int belongsTo) {
        return new AutoValue_UserDeviceDbEntity.Builder()
                .id(id)
                .version(version)
                .validTimeStart(validTimeStart)
                .validTimeEnd(validTimeEnd)
                .transactionTimeStart(transactionTimeStart)
                .transactionTimeEnd(transactionTimeEnd)
                .initiates(initiates)
                .name(name)
                .belongsTo(belongsTo)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked") // Builder always complies to parent class
    abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends ServerDbEntity.Builder<UserDeviceDbEntity, Builder> implements UserDeviceFields.Builder<UserDeviceDbEntity, Builder> {

        public abstract UserDeviceDbEntity build();
    }
}
