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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;
import de.njsm.stocks.client.database.NullablePreservedId;
import de.njsm.stocks.client.database.PreservedId;

import java.time.Period;

@AutoValue
@Entity(tableName = "food_to_add")
public abstract class FoodAddEntity implements IdFields {

    @ColumnInfo(name = "name")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract String name();

    @ColumnInfo(name = "to_buy")
    @AutoValue.CopyAnnotations
    public abstract boolean toBuy();

    @ColumnInfo(name = "expiration_offset")
    @AutoValue.CopyAnnotations
    public abstract Period expirationOffset();

    @Embedded(prefix = "location_")
    @AutoValue.CopyAnnotations
    public abstract NullablePreservedId location();

    @Embedded(prefix = "store_unit_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId storeUnit();

    @ColumnInfo(name = "description")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract String description();

    public static FoodAddEntity create(int id, String name, boolean toBuy, Period expirationOffset, NullablePreservedId location,  PreservedId storeUnit, String description) {
        return new AutoValue_FoodAddEntity(id, name, toBuy, expirationOffset, location, storeUnit, description);
    }

    @Ignore
    public static FoodAddEntity create(String name, boolean toBuy, Period expirationOffset, NullablePreservedId location,  PreservedId storeUnit, String description) {
        return new AutoValue_FoodAddEntity(0, name, toBuy, expirationOffset, location, storeUnit, description);
    }
}
