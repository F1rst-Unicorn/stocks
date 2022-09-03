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
import de.njsm.stocks.client.business.FoodAddInteractor;
import de.njsm.stocks.client.business.entities.FoodAddForm;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import javax.inject.Inject;
import java.util.List;

public class FoodAddViewModel extends ViewModel {

    private final FoodAddInteractor interactor;

    @Inject
    FoodAddViewModel(FoodAddInteractor interactor) {
        this.interactor = interactor;
    }

    public void add(FoodAddForm data) {
        interactor.add(data);
    }

    public LiveData<List<ScaledUnitForListing>> getUnits() {
        return LiveDataReactiveStreams.fromPublisher(
                interactor.getUnits().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public LiveData<List<LocationForSelection>> getLocations() {
        return LiveDataReactiveStreams.fromPublisher(
                interactor.getLocations().toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
