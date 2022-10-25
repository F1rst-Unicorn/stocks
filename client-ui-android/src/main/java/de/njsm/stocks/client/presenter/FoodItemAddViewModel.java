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
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodItemAddData;
import de.njsm.stocks.client.business.entities.FoodItemForm;
import de.njsm.stocks.client.business.entities.Identifiable;
import io.reactivex.rxjava3.core.BackpressureStrategy;

public class FoodItemAddViewModel extends ViewModel {

    private final FoodItemAddInteractor interactor;

    public FoodItemAddViewModel(FoodItemAddInteractor interactor) {
        this.interactor = interactor;
    }

    public LiveData<FoodItemAddData> getFormData(Identifiable<Food> food) {
        return LiveDataReactiveStreams.fromPublisher(
                interactor.getFormData(food).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void add(FoodItemForm data) {
        interactor.add(data);
    }
}
