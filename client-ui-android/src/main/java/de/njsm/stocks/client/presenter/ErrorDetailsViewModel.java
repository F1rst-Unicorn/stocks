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
import de.njsm.stocks.client.business.ErrorListInteractor;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;

import javax.inject.Inject;

public class ErrorDetailsViewModel extends ViewModel {

    private final ErrorListInteractor errorListInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableDataCache<ErrorDescription> data;

    @Inject
    public ErrorDetailsViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor, ObservableDataCache<ErrorDescription> data) {
        this.errorListInteractor = errorListInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
        this.data = data;
    }

    public LiveData<ErrorDescription> getError(long errorDescriptionId) {
        return data.getLiveData(() -> errorListInteractor.getError(errorDescriptionId));
    }

    public void retry() {
        data.performOnCurrentData(errorRetryInteractor::retry);
    }

    public void delete() {
        data.performOnCurrentData(errorRetryInteractor::delete);
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
