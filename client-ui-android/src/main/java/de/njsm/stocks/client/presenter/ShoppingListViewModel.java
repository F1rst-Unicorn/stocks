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
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToBuy;
import de.njsm.stocks.client.business.entities.FoodWithAmountForListing;
import de.njsm.stocks.client.business.entities.Id;

import java.util.List;
import java.util.function.Consumer;

public class ShoppingListViewModel extends ViewModel {

    private final Synchroniser synchroniser;

    private final FoodToBuyInteractor interactor;

    private final ObservableListCache<FoodWithAmountForListing> data;

    ShoppingListViewModel(Synchroniser synchroniser, FoodToBuyInteractor interactor, ObservableListCache<FoodWithAmountForListing> data) {
        this.synchroniser = synchroniser;
        this.interactor = interactor;
        this.data = data;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public void removeFromShoppingList(int listItemIndex) {
        data.performOnListItem(listItemIndex, v -> interactor.manageFoodToBuy(FoodToBuy.removeFromShoppingList(v.id())));
    }

    public void resolveId(int listItemIndex, Consumer<Id<Food>> callback) {
        data.performOnListItem(listItemIndex, callback::accept);
    }

    public LiveData<List<FoodWithAmountForListing>> getFood() {
        return data.getLiveData(interactor::getFoodToBuy);
    }
}
