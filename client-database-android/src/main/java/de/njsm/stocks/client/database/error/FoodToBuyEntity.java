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
@Entity(tableName = "food_to_buy")
public abstract class FoodToBuyEntity implements IdFields, VersionFields {

    @Embedded(prefix = "food_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId food();

    @ColumnInfo(name = "to_buy")
    @AutoValue.CopyAnnotations
    public abstract boolean toBuy();

    @ColumnInfo(name = "execution_time")
    @AutoValue.CopyAnnotations
    public abstract Instant executionTime();

    public static FoodToBuyEntity create(int id, PreservedId food, int version, boolean toBuy, Instant executionTime) {
        return new AutoValue_FoodToBuyEntity(id, version, food, toBuy, executionTime);
    }

    @Ignore
    public static FoodToBuyEntity create(PreservedId food, int version, boolean toBuy, Instant executionTime) {
        return new AutoValue_FoodToBuyEntity(0, version, food, toBuy, executionTime);
    }
}
