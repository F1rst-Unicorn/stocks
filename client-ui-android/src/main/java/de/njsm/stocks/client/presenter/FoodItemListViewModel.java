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
import de.njsm.stocks.client.business.FoodItemListInteractor;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.FoodItemsForListing;
import de.njsm.stocks.client.business.entities.Id;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class FoodItemListViewModel extends ViewModel {

    private final FoodItemListInteractor interactor;

    private final EntityDeleter<FoodItem> deleter;

    private Observable<FoodItemsForListing> data;

    @Inject
    FoodItemListViewModel(FoodItemListInteractor interactor, EntityDeleter<FoodItem> deleter) {
        this.interactor = interactor;
        this.deleter = deleter;
    }

    public void delete(int listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list -> deleter.delete(list.foodItems().get(listItemIndex)));
    }

    public void resolveId(int listItemIndex, Consumer<Integer> callback) {
        if (data == null)
            return;

        performOnCurrentData(list -> callback.accept(list.foodItems().get(listItemIndex).id()));
    }

    private void performOnCurrentData(Consumer<FoodItemsForListing> runnable) {
        data.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }
    public LiveData<FoodItemsForListing> get(Id<Food> id) {
        return LiveDataReactiveStreams.fromPublisher(getData(id)
                .toFlowable(BackpressureStrategy.LATEST));
    }

    public Observable<FoodItemsForListing> getData(Id<Food> id) {
        if (data == null)
            data = interactor.get(id);
        return data;
    }
}
