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
import de.njsm.stocks.client.business.FoodEditInteractor;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodEditingFormData;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.business.entities.Identifiable;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class FoodEditViewModel extends ViewModel {

    private final FoodEditInteractor interactor;

    private Observable<FoodEditingFormData> data;

    @Inject
    FoodEditViewModel(FoodEditInteractor interactor) {
        this.interactor = interactor;
    }

    public void edit(FoodToEdit data) {
        interactor.edit(data);
    }

    public LiveData<FoodEditingFormData> getFormData(Identifiable<Food> id) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(id).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    private Observable<FoodEditingFormData> getData(Identifiable<Food> id) {
        if (data == null)
            data = interactor.getFormData(id);
        return data;
    }
}
