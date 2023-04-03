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
import de.njsm.stocks.client.business.LocationConflictInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.LocationEditErrorDetails;
import de.njsm.stocks.client.business.entities.LocationToEdit;
import de.njsm.stocks.client.business.entities.conflict.LocationEditConflictData;


public class LocationConflictViewModel extends ViewModel {

    private final LocationConflictInteractor locationConflictInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableDataCache<LocationEditConflictData> data;

    LocationConflictViewModel(LocationConflictInteractor locationConflictInteractor, ErrorRetryInteractor errorRetryInteractor, ObservableDataCache<LocationEditConflictData> data) {
        this.locationConflictInteractor = locationConflictInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
        this.data = data;
    }

    public LiveData<LocationEditConflictData> getLocationEditConflict(long errorId) {
        return data.getLiveData(() -> locationConflictInteractor.getLocationEditConflict(errorId));
    }

    public void editLocation(LocationToEdit data) {
        this.data.performOnCurrentData(v -> {
                ErrorDescription errorToRetry = ErrorDescription.minimal(
                        v.errorId(),
                        LocationEditErrorDetails.create(data.id(), data.name(), data.description())
                );
                errorRetryInteractor.retry(errorToRetry);
        });
    }
}
