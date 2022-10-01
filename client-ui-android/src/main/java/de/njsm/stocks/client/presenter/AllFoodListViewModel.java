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
import de.njsm.stocks.client.business.AllPresentFoodListInteractor;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodForListing;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class AllFoodListViewModel extends AbstractFoodListViewModel {

    private final AllPresentFoodListInteractor allPresentFoodListInteractor;

    @Inject
    AllFoodListViewModel(Synchroniser synchroniser, AllPresentFoodListInteractor allPresentFoodListInteractor, EntityDeleter<Food> deleter) {
        super(synchroniser, deleter);
        this.allPresentFoodListInteractor = allPresentFoodListInteractor;
    }

    public LiveData<List<FoodForListing>> getFood() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    private Observable<List<FoodForListing>> getData() {
        if (data == null)
            data = allPresentFoodListInteractor.getFood();
        return data;
    }
}
