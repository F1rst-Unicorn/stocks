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
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FoodItemListInteractor;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;
import java.util.function.Consumer;

public class FoodItemListViewModel extends ViewModel {

    private final FoodItemListInteractor interactor;

    private final EntityDeleter<FoodItem> deleter;

    private final FoodToBuyInteractor toBuyInteractor;

    private final ObservableDataCache<FoodItemsForListing> data;

    @Inject
    FoodItemListViewModel(FoodItemListInteractor interactor, EntityDeleter<FoodItem> deleter, FoodToBuyInteractor toBuyInteractor, ObservableDataCache<FoodItemsForListing> data) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.toBuyInteractor = toBuyInteractor;
        this.data = data;
    }

    public LiveData<FoodItemsForListing> get(Id<Food> id) {
        return data.getLiveData(() -> interactor.get(id));
    }

    public void delete(int listItemIndex) {
        data.performOnNestedList(listItemIndex, FoodItemsForListing::foodItems, deleter::delete);
    }

    public void resolveId(int listItemIndex, Consumer<Id<FoodItem>> callback) {
        data.performOnNestedList(listItemIndex, FoodItemsForListing::foodItems, callback::accept);
    }

    public void toggleShoppingFlag(Id<Food> foodId) {
        toBuyInteractor.manageFoodToBuy(FoodToToggleBuy.create(foodId.id()));
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
