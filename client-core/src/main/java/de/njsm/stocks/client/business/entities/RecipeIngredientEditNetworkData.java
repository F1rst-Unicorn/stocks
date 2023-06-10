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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RecipeIngredientEditNetworkData implements Versionable<RecipeIngredient> {

    public abstract int amount();

    public abstract Id<ScaledUnit> unit();

    public abstract Id<Food> ingredient();

    public static RecipeIngredientEditNetworkData create(int id, int version, int amount, Id<ScaledUnit> unit, Id<Food> ingredient) {
        return new AutoValue_RecipeIngredientEditNetworkData(id, version, amount, unit, ingredient);
    }
}
