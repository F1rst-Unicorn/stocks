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
import de.njsm.stocks.client.business.UnitEditInteractor;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class UnitEditViewModel extends ViewModel {

    private final UnitEditInteractor unitEditInteractor;

    private Observable<UnitToEdit> data;

    @Inject
    UnitEditViewModel(UnitEditInteractor unitEditInteractor) {
        this.unitEditInteractor = unitEditInteractor;
    }

    public LiveData<UnitToEdit> get(Identifiable<Unit> id) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(id).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void edit(UnitToEdit data) {
        unitEditInteractor.edit(data);
    }

    private Observable<UnitToEdit> getData(Identifiable<Unit> id) {
        if (data == null)
            data = unitEditInteractor.get(id);
        return data;
    }
}
