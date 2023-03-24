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

package de.njsm.stocks.client.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;

@AutoValue
@Entity(tableName = "ticket")
public abstract class TicketEntity implements IdFields {

    @ColumnInfo(name = "ticket")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract String ticket();

    @ColumnInfo(name = "device_id")
    @AutoValue.CopyAnnotations
    public abstract int deviceId();

    public static TicketEntity create(int id, String ticket, int deviceId) {
        return new AutoValue_TicketEntity(id, ticket, deviceId);
    }

    @Ignore
    public static TicketEntity create(String ticket, int deviceId) {
        return new AutoValue_TicketEntity(0, ticket, deviceId);
    }
}
