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
import java.util.List;

class InMemoryRecipeCookInteractorImpl implements RecipeCookInteractor {

    @Inject
    InMemoryRecipeCookInteractorImpl() {
    }

    @Override
    public Observable<RecipeCookingFormData> getData(IdImpl<Recipe> recipeId) {
        return Observable.just(RecipeCookingFormData.create(
                IdImpl.create(1),
                "Pizza",
                List.of(RecipeCookingFormDataIngredient.create(
                        IdImpl.create(2),
                        "Cheese",
                        false,
                        List.of(RecipeCookingFormDataIngredient.Amount.create(BigDecimal.TEN, "g")),
                        List.of(RecipeCookingFormDataIngredient.PresentAmount.create(
                                RecipeCookingFormDataIngredient.Amount.create(BigDecimal.TEN, "g"),
                                IdImpl.create(3),
                                4, 2
                        ))
                )),
                List.of()
        ));
    }

    @Override
    public void cook(RecipeCookingForm recipeCookingForm) {

    }
}
