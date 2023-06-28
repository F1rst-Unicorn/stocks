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
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.SearchInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToBuy;
import de.njsm.stocks.client.business.entities.SearchedFoodForListing;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class SearchedFoodViewModel extends ViewModel {

    private final SearchInteractor interactor;

    private final EntityDeleter<Food> deleter;

    private final Synchroniser synchroniser;

    private final ObservableListCache<SearchedFoodForListing> data;

    private final FoodToBuyInteractor toBuyInteractor;

    @Inject
    SearchedFoodViewModel(Synchroniser synchroniser,
                          SearchInteractor interactor,
                          EntityDeleter<Food> deleter,
                          FoodToBuyInteractor toBuyInteractor,
                          ObservableListCache<SearchedFoodForListing> data) {
        this.synchroniser = synchroniser;
        this.interactor = interactor;
        this.deleter = deleter;
        this.toBuyInteractor = toBuyInteractor;
        this.data = data;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<List<SearchedFoodForListing>> getFood(String query) {
        return data.getLiveData(() -> interactor.get(query));
    }

    public void delete(int listItemIndex) {
        data.performOnListItem(listItemIndex, deleter::delete);
    }

    public void addToShoppingList(int listItemIndex) {
        data.performOnListItem(listItemIndex, v ->
                toBuyInteractor.manageFoodToBuy(FoodToBuy.putOnShoppingList(v.id())));
    }

    public void resolveId(int listItemIndex, Consumer<Integer> callback) {
        data.performOnListItem(listItemIndex, v -> callback.accept(v.id()));
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
