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
import de.njsm.stocks.client.business.CrashListInteractor;
import de.njsm.stocks.client.business.entities.CrashLog;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.function.Consumer;

public class CrashLogListViewModel extends ViewModel {

    private final CrashListInteractor interactor;

    private Observable<List<CrashLog>> data;

    CrashLogListViewModel(CrashListInteractor interactor) {
        this.interactor = interactor;
    }

    public void delete(int listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list -> interactor.delete(list.get(listItemIndex)));
    }

    public void resolve(int listItemIndex, Consumer<CrashLog> callback) {
        if (data == null)
            return;

        performOnCurrentData(list -> callback.accept(list.get(listItemIndex)));
    }

    private void performOnCurrentData(Consumer<List<CrashLog>> runnable) {
        data.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    public LiveData<List<CrashLog>> get() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    private Observable<List<CrashLog>> getData() {
        if (data == null)
            data = interactor.get();
        return data;
    }
}
