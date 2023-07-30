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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.RecipeCookInteractor;
import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;

public class RecipeCookViewModel extends ViewModel {

    private final RecipeCookInteractor interactor;

    private final FoodToBuyInteractor foodToBuyInteractor;

    private final ObservableDataCache<RecipeCookingFormData> data;

    @Inject
    RecipeCookViewModel(RecipeCookInteractor interactor, FoodToBuyInteractor foodToBuyInteractor, ObservableDataCache<RecipeCookingFormData> data) {
        this.interactor = interactor;
        this.foodToBuyInteractor = foodToBuyInteractor;
        this.data = data;
    }

    public LiveData<RecipeCookingFormData> get(IdImpl<Recipe> recipeId) {
        return data.getLiveData(() -> interactor.getData(recipeId));
    }

    public void putFoodToBuy(IdImpl<Food> foodId) {
        foodToBuyInteractor.manageFoodToBuy(FoodToToggleBuy.create(foodId));
    }

    public void cookRecipe(RecipeCookingForm recipeCookingForm) {
        interactor.cook(recipeCookingForm);
    }
}
