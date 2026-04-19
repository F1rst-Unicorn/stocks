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
import de.njsm.stocks.client.business.FoodItemAddInteractor;
import de.njsm.stocks.client.business.PriceAddInteractor;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import java.util.List;

public class FoodItemAddViewModel extends ViewModel {

    private final FoodItemAddInteractor interactor;

    private final PriceAddInteractor priceInteractor;

    FoodItemAddViewModel(FoodItemAddInteractor interactor, PriceAddInteractor priceInteractor) {
        this.interactor = interactor;
        this.priceInteractor = priceInteractor;
    }

    public LiveData<FoodItemAddData> getFormData(Id<Food> food) {
        return LiveDataReactiveStreams.fromPublisher(
                interactor.getFormData(food).toFlowable()
        );
    }

    public void add(FoodItemForm data) {
        interactor.add(data);
    }

    public void add(PriceAddForm data) {
        priceInteractor.addPrice(data);
    }


    public LiveData<List<ScaledUnitForSelection>> getUnits() {
        return LiveDataReactiveStreams.fromPublisher(
                priceInteractor.getUnits().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<List<GroceryStoreForSelection>> getGroceryStores() {
        return LiveDataReactiveStreams.fromPublisher(
                priceInteractor.getGroceryStores().toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
