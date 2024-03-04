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

import java.io.Serializable;

@AutoValue
public abstract class RecipeIngredientToAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract int amount();

    public abstract Id<Food> ingredient();

    public abstract Id<ScaledUnit> unit();

    public static RecipeIngredientToAdd create(int amount, Id<Food> ingredient, Id<ScaledUnit> unit) {
        return new AutoValue_RecipeIngredientToAdd(amount, ingredient, unit);
    }

    public static RecipeIngredientToAdd create(int amount, int ingredient, int unit) {
        return new AutoValue_RecipeIngredientToAdd(amount, IdImpl.create(ingredient), IdImpl.create(unit));
    }
}
