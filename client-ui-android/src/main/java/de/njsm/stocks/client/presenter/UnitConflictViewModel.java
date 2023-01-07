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
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.UnitConflictInteractor;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.conflict.UnitEditConflictData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;


public class UnitConflictViewModel extends ViewModel {

    private final UnitConflictInteractor unitConflictInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private Observable<UnitEditConflictData> data;

    UnitConflictViewModel(UnitConflictInteractor unitConflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        this.unitConflictInteractor = unitConflictInteractor;
        this.errorRetryInteractor = errorRetryInteractor;
    }

    public LiveData<UnitEditConflictData> getUnitEditConflict(long errorId) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(errorId).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void edit(UnitToEdit editedData) {
        if (this.data != null)
            this.data.firstElement()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        ErrorDescription errorToRetry = ErrorDescription.minimal(
                                v.errorId(),
                                UnitEditErrorDetails.create(editedData.id(), editedData.name(), editedData.abbreviation())
                        );
                        errorRetryInteractor.retry(errorToRetry);
                    });
    }

    private Observable<UnitEditConflictData> getData(long errorId) {
        if (data == null)
            data = unitConflictInteractor.getUnitEditConflict(errorId);
        return data;
    }
}
