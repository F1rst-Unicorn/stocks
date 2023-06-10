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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.testdata.FoodsForListing;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

class InMemoryRecipeEditInteractorImpl implements RecipeEditInteractor {

    private final BehaviorSubject<List<EmptyFood>> data;

    @Inject
    InMemoryRecipeEditInteractorImpl(FoodsForListing foodsForListing) {
        this.data = foodsForListing.getData();
    }

    @Override
    public Maybe<RecipeEditFormData> getForm(Id<Recipe> recipeId) {
        return Maybe.just(RecipeEditFormData.create(
                RecipeEditBaseData.create(
                        4,
                        "Pizza",
                        "just bake",
                        Duration.ofHours(2)
                ),
                List.of(
                        RecipeIngredientEditFormData.create(
                                4,
                                1,
                                1,
                                IdImpl.create(2),
                                1,
                                IdImpl.create(7)
                        ),
                        RecipeIngredientEditFormData.create(
                                5,
                                2,
                                0,
                                IdImpl.create(1),
                                0,
                                IdImpl.create(3)
                        )
                ),
                List.of(
                        RecipeProductEditFormData.create(
                                3,
                                1,
                                2,
                                IdImpl.create(4),
                                2,
                                IdImpl.create(12)
                        )
                ),
                List.of(
                        FoodForSelection.create(3, "Banana"),
                        FoodForSelection.create(7, "Flour"),
                        FoodForSelection.create(12, "Water")
                ),
                ScaledUnitsForSelection.generate()));
    }

    @Override
    public void edit(RecipeEditForm form) {

    }
}
