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
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.UnitListInteractor;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.entities.UnitForListing;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class UnitListViewModel extends ViewModel {

    private final UnitListInteractor unitListInteractor;

    private final EntityDeleter<Unit> unitDeleter;

    private Observable<List<UnitForListing>> data;

    @Inject
    UnitListViewModel(UnitListInteractor unitListInteractor, EntityDeleter<Unit> unitDeleter) {
        this.unitListInteractor = unitListInteractor;
        this.unitDeleter = unitDeleter;
    }

    public LiveData<List<UnitForListing>> getUnits() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void deleteUnit(int listItemIndex) {
        performOnCurrentUnits(list -> unitDeleter.delete(list.get(listItemIndex)));
    }

    public void resolveUnitId(int listItemIndex, Consumer<Integer> callback) {
        performOnCurrentUnits(list -> callback.accept(list.get(listItemIndex).id()));
    }

    private void performOnCurrentUnits(Consumer<List<UnitForListing>> runnable) {
        getData()
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    private Observable<List<UnitForListing>> getData() {
        if (data == null)
            data = unitListInteractor.getUnits();
        return data;
    }
}
