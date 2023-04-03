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
import de.njsm.stocks.client.business.ScaledUnitConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ScaledUnitEditErrorDetails;
import de.njsm.stocks.client.business.entities.ScaledUnitToEdit;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictFormData;

import javax.inject.Inject;

public class ScaledUnitConflictViewModel extends ViewModel {

    private final ScaledUnitConflictInteractor scaledUnitConflictInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableDataCache<ScaledUnitEditConflictFormData> data;

    @Inject
    ScaledUnitConflictViewModel(ScaledUnitConflictInteractor scaledUnitConflictInteractor, ErrorRetryInteractor errorRetryInteractor, ObservableDataCache<ScaledUnitEditConflictFormData> data) {
        this.scaledUnitConflictInteractor = scaledUnitConflictInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
        this.data = data;
    }

    public LiveData<ScaledUnitEditConflictFormData> getScaledUnitEditConflict(long errorId) {
        return data.getLiveData(() -> scaledUnitConflictInteractor.getScaledUnitEditConflict(errorId));
    }

    public void edit(ScaledUnitToEdit editedScaledUnit) {
        data.performOnCurrentData(v -> {
                ErrorDescription errorToRetry = ErrorDescription.minimal(
                        v.errorId(),
                        ScaledUnitEditErrorDetails.create(
                                editedScaledUnit.id(),
                                editedScaledUnit.scale(),
                                editedScaledUnit.unit(),
                                "", ""
                        )
                );
                errorRetryInteractor.retry(errorToRetry);
        });
    }
}
