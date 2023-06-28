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
import de.njsm.stocks.client.business.FoodConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.FoodEditErrorDetails;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.business.entities.conflict.FoodEditConflictFormData;

import javax.inject.Inject;

public class FoodConflictViewModel extends ViewModel {

    private final FoodConflictInteractor conflictInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableDataCache<FoodEditConflictFormData> data;

    @Inject
    FoodConflictViewModel(FoodConflictInteractor conflictInteractor, ErrorRetryInteractor errorRetryInteractor, ObservableDataCache<FoodEditConflictFormData> data) {
        this.conflictInteractor = conflictInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
        this.data = data;
    }

    public LiveData<FoodEditConflictFormData> getFoodEditConflict(long errorId) {
        return data.getLiveData(() -> conflictInteractor.getEditConflict(errorId));
    }

    public void edit(FoodToEdit editedFood) {
        data.performOnCurrentData(v -> {
                ErrorDescription errorToRetry = ErrorDescription.minimal(
                        v.errorId(),
                        FoodEditErrorDetails.create(
                                editedFood.id(),
                                editedFood.name(),
                                editedFood.toBuy(),
                                editedFood.expirationOffset(),
                                editedFood.location(),
                                editedFood.storeUnit(),
                                editedFood.description()
                        )
                );
                errorRetryInteractor.retry(errorToRetry);
        });
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
