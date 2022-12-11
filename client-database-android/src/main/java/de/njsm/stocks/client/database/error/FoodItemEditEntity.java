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
import de.njsm.stocks.client.database.PreservedId;
import de.njsm.stocks.client.database.VersionFields;

import java.time.Instant;

@AutoValue
@Entity(tableName = "food_item_to_edit")
public abstract class FoodItemEditEntity implements IdFields, VersionFields {

    @Embedded(prefix = "food_item_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId foodItem();

    @ColumnInfo(name = "eat_by")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant eatBy();

    @Embedded(prefix = "stored_in_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId storedIn();

    @Embedded(prefix = "unit_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId unit();

    @ColumnInfo(name = "execution_time")
    @AutoValue.CopyAnnotations
    public abstract Instant executionTime();

    public static FoodItemEditEntity create(int id, int version, PreservedId foodItem, Instant eatBy, PreservedId storedIn, PreservedId unit, Instant executionTime) {
        return new AutoValue_FoodItemEditEntity(id, version, foodItem, eatBy, storedIn, unit, executionTime);
    }

    @Ignore
    public static FoodItemEditEntity create(int version, PreservedId foodItem, Instant eatBy, PreservedId storedIn, PreservedId unit, Instant executionTime) {
        return new AutoValue_FoodItemEditEntity(0, version, foodItem, eatBy, storedIn, unit, executionTime);
    }
}
