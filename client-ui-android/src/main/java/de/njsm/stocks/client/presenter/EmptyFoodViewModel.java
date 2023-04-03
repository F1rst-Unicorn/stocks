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
import de.njsm.stocks.client.business.EmptyFoodInteractor;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToBuy;

import java.util.List;
import java.util.function.Consumer;

public class EmptyFoodViewModel extends ViewModel {

    private final Synchroniser synchroniser;

    private final EmptyFoodInteractor emptyFoodInteractor;

    private final EntityDeleter<Food> deleter;

    private final FoodToBuyInteractor toBuyInteractor;

    private final ObservableListCache<EmptyFood> data;

    public EmptyFoodViewModel(Synchroniser synchroniser, EmptyFoodInteractor emptyFoodInteractor, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor, ObservableListCache<EmptyFood> data) {
        this.synchroniser = synchroniser;
        this.emptyFoodInteractor = emptyFoodInteractor;
        this.deleter = deleter;
        this.toBuyInteractor = toBuyInteractor;
        this.data = data;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<List<EmptyFood>> getFood() {
        return data.getLiveData(emptyFoodInteractor::get);
    }

    public void delete(int listItemIndex) {
        data.performOnListItem(listItemIndex, deleter::delete);
    }

    public void putOnShoppingList(int listItemIndex) {
        data.performOnListItem(listItemIndex, v ->
                toBuyInteractor.manageFoodToBuy(FoodToBuy.putOnShoppingList(v.id())));
    }

    public void resolveId(int listItemIndex, Consumer<Integer> callback) {
        data.performOnListItem(listItemIndex, v -> callback.accept(v.id()));
    }
}
