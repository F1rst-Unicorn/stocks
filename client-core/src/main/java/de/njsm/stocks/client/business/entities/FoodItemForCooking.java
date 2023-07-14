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
public abstract class FoodItemForCooking {

    public abstract IdImpl<Food> ofType();

    public abstract IdImpl<Unit> unit();

    public abstract String abbreviation();

    public abstract IdImpl<ScaledUnit> scaledUnit();

    public abstract BigDecimal scale();

    public abstract int presentCount();

    public RecipeIngredientAmountDistributor.PresentAmount toPresentAmount() {
        return RecipeIngredientAmountDistributor.PresentAmount.create(unit(), scaledUnit(), scale(), presentCount());
    }

    public RecipeCookingFormDataIngredient.PresentAmount toFormDataPresentAmount(int selectedCount) {
        return RecipeCookingFormDataIngredient.PresentAmount.create(
                RecipeCookingFormDataIngredient.Amount.create(scale(), abbreviation()),
                presentCount(),
                selectedCount);
    }

    public static FoodItemForCooking create(IdImpl<Food> ofType,
                                            IdImpl<Unit> unit,
                                            String abbreviation,
                                            IdImpl<ScaledUnit> scaledUnit,
                                            BigDecimal scale,
                                            int presentCount) {
        return new AutoValue_FoodItemForCooking(ofType, unit, abbreviation, scaledUnit, scale, presentCount);
    }
}
