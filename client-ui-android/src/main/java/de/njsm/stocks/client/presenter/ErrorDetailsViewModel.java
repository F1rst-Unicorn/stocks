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
import de.njsm.stocks.client.business.ErrorListInteractor;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class ErrorDetailsViewModel extends ViewModel {

    private final ErrorListInteractor errorListInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private Observable<ErrorDescription> data;

    @Inject
    public ErrorDetailsViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor) {
        this.errorListInteractor = errorListInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
    }

    public LiveData<ErrorDescription> getError(long errorDescriptionId) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(errorDescriptionId).toFlowable(BackpressureStrategy.LATEST));
    }

    private Observable<ErrorDescription> getData(long errorDescriptionId) {
        if (data == null)
            data = errorListInteractor.getError(errorDescriptionId);
        return data;
    }

    public void retry() {
        if (data != null)
            data.firstElement()
                    .subscribe(errorRetryInteractor::retry);
    }

    public void delete() {
        if (data != null)
            data.firstElement()
                    .subscribe(errorRetryInteractor::delete);
    }
}
