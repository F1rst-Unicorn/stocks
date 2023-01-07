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
import com.google.auto.value.AutoValue;

import java.time.Instant;

public interface FoodItemFields {

    @ColumnInfo(name = "eat_by")
    @NonNull
    @AutoValue.CopyAnnotations
    Instant eatBy();

    @ColumnInfo(name = "of_type")
    @NonNull
    @AutoValue.CopyAnnotations
    int ofType();

    @ColumnInfo(name = "stored_in")
    @NonNull
    @AutoValue.CopyAnnotations
    int storedIn();

    @ColumnInfo(name = "buys")
    @NonNull
    @AutoValue.CopyAnnotations
    int buys();

    @ColumnInfo(name = "registers")
    @NonNull
    @AutoValue.CopyAnnotations
    int registers();

    @ColumnInfo(name = "unit")
    @NonNull
    @AutoValue.CopyAnnotations
    int unit();

    interface Builder<T extends ServerDbEntity<T>, B extends ServerDbEntity.Builder<T, B>> {
        B eatBy(Instant v);

        B ofType(int v);

        B storedIn(int v);

        B buys(int v);

        B registers(int v);

        B unit(int v);
    }
}
