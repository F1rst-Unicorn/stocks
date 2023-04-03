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
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.FoodItemConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.FoodItemEditErrorDetails;
import de.njsm.stocks.client.business.entities.FoodItemToEdit;
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictFormData;

import javax.inject.Inject;

public class FoodItemConflictViewModel extends ViewModel {

    private final FoodItemConflictInteractor conflictInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableDataCache<FoodItemEditConflictFormData> data;

    @Inject
    FoodItemConflictViewModel(FoodItemConflictInteractor conflictInteractor, ErrorRetryInteractor errorRetryInteractor, ObservableDataCache<FoodItemEditConflictFormData> data) {
        this.conflictInteractor = conflictInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
        this.data = data;
    }

    public LiveData<FoodItemEditConflictFormData> getFoodEditConflict(long errorId) {
        return data.getLiveData(() -> conflictInteractor.getEditConflict(errorId));
    }

    public void edit(FoodItemToEdit editedFoodItem) {
        data.performOnCurrentData(v -> {
                ErrorDescription errorToRetry = ErrorDescription.minimal(
                        v.errorId(),
                        FoodItemEditErrorDetails.create(
                                editedFoodItem.id(),
                                "",
                                editedFoodItem.eatBy(),
                                editedFoodItem.storedIn(),
                                editedFoodItem.unit()
                        )
                );
                errorRetryInteractor.retry(errorToRetry);
        });
    }
}
