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

import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
@Entity(tableName = "price_to_add")
public abstract class PriceAddEntity implements IdFields {

    @ColumnInfo(name = "price")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract BigDecimal price();

    @ColumnInfo(name = "scale")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract BigDecimal scale();

    @ColumnInfo(name = "valid_time")
    @NonNull
    @AutoValue.CopyAnnotations
    public abstract Instant validTime();

    @Embedded(prefix = "grocery_store_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId groceryStore();

    @Embedded(prefix = "food_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId food();

    @Embedded(prefix = "scaled_unit_")
    @AutoValue.CopyAnnotations
    public abstract PreservedId scaledUnit();

    public static PriceAddEntity create(int id, BigDecimal price, BigDecimal scale, Instant validTime, PreservedId groceryStore, PreservedId food, PreservedId scaledUnit) {
        return new AutoValue_PriceAddEntity(id, price, scale, validTime, groceryStore, food, scaledUnit);
    }

    @Ignore
    public static PriceAddEntity create(BigDecimal price, BigDecimal scale, Instant validTime, PreservedId groceryStore, PreservedId food, PreservedId scaledUnit) {
        return new AutoValue_PriceAddEntity(0, price, scale, validTime, groceryStore, food, scaledUnit);
    }
}
