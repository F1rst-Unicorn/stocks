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
import de.njsm.stocks.client.business.RecipeIngredientAmountDistributor;

import java.math.BigDecimal;

@AutoValue
public abstract class RecipeIngredientForCooking {

    public abstract IdImpl<Food> food();

    public abstract String foodName();

    public abstract boolean toBuy();

    public abstract IdImpl<Unit> unit();

    public abstract String abbreviation();

    public abstract BigDecimal amount();

    public RecipeIngredientAmountDistributor.RequiredAmount toRequiredAmount() {
        return RecipeIngredientAmountDistributor.RequiredAmount.create(unit(), amount());
    }

    public RecipeCookingFormDataIngredient.Amount toFormDataRequiredAmount() {
        return RecipeCookingFormDataIngredient.Amount.create(amount(), abbreviation());
    }

    public static RecipeIngredientForCooking create(IdImpl<Food> food,
                                                    String foodName,
                                                    boolean toBuy,
                                                    IdImpl<Unit> unit,
                                                    String abbreviation,
                                                    BigDecimal amount) {
        return new AutoValue_RecipeIngredientForCooking(food, foodName, toBuy, unit, abbreviation, amount);
    }
}
