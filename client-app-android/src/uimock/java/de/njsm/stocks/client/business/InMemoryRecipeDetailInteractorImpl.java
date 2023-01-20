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
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

class InMemoryRecipeDetailInteractorImpl implements RecipeDetailInteractor {

    @Inject
    InMemoryRecipeDetailInteractorImpl() {
    }

    @Override
    public Observable<RecipeForDetails> get(Id<Recipe> recipeId) {
        return Observable.just(RecipeForDetails.create(
                1,
                "Pizza",
                Duration.ofMinutes(45),
                "Just do it",
                List.of(
                        RecipeIngredientForDetails.create(
                                1,
                                "Tomato",
                                UnitAmount.of(BigDecimal.ONE, "piece"),
                                List.of(UnitAmount.of(BigDecimal.TEN, "piece")))
                ),
                List.of(
                        RecipeProductForDetails.create(
                                1,
                                "Pizza",
                                UnitAmount.of(BigDecimal.ONE, "bite"),
                                List.of(UnitAmount.of(BigDecimal.TEN, "bite")))
                )
        ));
    }
}
