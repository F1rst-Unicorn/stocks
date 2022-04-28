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

public interface RecipeIngredientFields {

    @ColumnInfo(name = "amount")
    @NonNull
    @AutoValue.CopyAnnotations
    int amount();

    @ColumnInfo(name = "ingredient")
    @NonNull
    @AutoValue.CopyAnnotations
    int ingredient();

    @ColumnInfo(name = "unit")
    @NonNull
    @AutoValue.CopyAnnotations
    int unit();

    @ColumnInfo(name = "recipe")
    @NonNull
    @AutoValue.CopyAnnotations
    int recipe();

    interface Builder<T extends ServerDbEntity<T>, B extends ServerDbEntity.Builder<T, B>> {

        B amount(int v);

        B ingredient(int v);

        B unit(int v);

        B recipe(int v);
    }
}
