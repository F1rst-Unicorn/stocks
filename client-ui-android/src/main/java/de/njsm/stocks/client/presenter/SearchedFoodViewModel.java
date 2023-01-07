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
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.FoodToBuyInteractor;
import de.njsm.stocks.client.business.SearchInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodToBuy;
import de.njsm.stocks.client.business.entities.SearchedFoodForListing;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class SearchedFoodViewModel extends ViewModel {

    private final SearchInteractor interactor;

    private final EntityDeleter<Food> deleter;

    private final Synchroniser synchroniser;

    private Observable<List<SearchedFoodForListing>> data;

    private final FoodToBuyInteractor toBuyInteractor;

    @Inject
    SearchedFoodViewModel(Synchroniser synchroniser, SearchInteractor interactor, EntityDeleter<Food> deleter, FoodToBuyInteractor toBuyInteractor) {
        this.synchroniser = synchroniser;
        this.interactor = interactor;
        this.deleter = deleter;
        this.toBuyInteractor = toBuyInteractor;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<List<SearchedFoodForListing>> getFood(String query) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(query).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void delete(int listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list -> deleter.delete(list.get(listItemIndex)));
    }

    public void addToShoppingList(Integer listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list ->
                toBuyInteractor.manageFoodToBuy(FoodToBuy.putOnShoppingList(list.get(listItemIndex).id())));

    }

    public void resolveId(int listItemIndex, Consumer<Integer> callback) {
        if (data == null)
            return;

        performOnCurrentData(list -> callback.accept(list.get(listItemIndex).id()));
    }

    private void performOnCurrentData(Consumer<List<SearchedFoodForListing>> runnable) {
        data.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    private Observable<List<SearchedFoodForListing>> getData(String query) {
        if (data == null)
            data = interactor.get(query);
        return data;
    }
}
