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
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import com.google.auto.value.AutoValue;

import java.time.Period;

public interface FoodFields {

    @ColumnInfo(name = "name")
    @NonNull
    @AutoValue.CopyAnnotations
    String name();

    @ColumnInfo(name = "to_buy")
    @NonNull
    @AutoValue.CopyAnnotations
    boolean toBuy();

    @ColumnInfo(name = "expiration_offset")
    @NonNull
    @AutoValue.CopyAnnotations
    Period expirationOffset();

    @ColumnInfo(name = "location")
    @Nullable
    @AutoValue.CopyAnnotations
    Integer location();

    @ColumnInfo(name = "store_unit")
    @NonNull
    @AutoValue.CopyAnnotations
    int storeUnit();

    @ColumnInfo(name = "description")
    @NonNull
    @AutoValue.CopyAnnotations
    String description();

    interface Builder<T extends ServerDbEntity<T>, B extends ServerDbEntity.Builder<T, B>> {
        B name(String v);

        B toBuy(boolean v);

        B expirationOffset(Period v);

        B location(@Nullable Integer v);

        B storeUnit(int v);

        B description(String v);
    }
}
