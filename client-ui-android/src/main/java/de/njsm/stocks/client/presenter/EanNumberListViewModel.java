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
import de.njsm.stocks.client.business.EanNumberListInteractor;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.function.Consumer;

public class EanNumberListViewModel extends ViewModel {

    private final EanNumberListInteractor interactor;

    private final EntityDeleter<EanNumber> deleter;

    private final Synchroniser synchroniser;

    private Observable<List<EanNumberForListing>> data;

    public EanNumberListViewModel(EanNumberListInteractor interactor, EntityDeleter<EanNumber> deleter, Synchroniser synchroniser) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.synchroniser = synchroniser;
    }


    public void synchronise() {
        synchroniser.synchronise();
    }

    public void delete(int listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list -> deleter.delete(list.get(listItemIndex)));
    }

    private void performOnCurrentData(Consumer<List<EanNumberForListing>> runnable) {
        data.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    public LiveData<List<EanNumberForListing>> get(Id<Food> food) {
        return LiveDataReactiveStreams.fromPublisher(
            getData(food).toFlowable(BackpressureStrategy.LATEST)
        );
    }

    private Observable<List<EanNumberForListing>> getData(Id<Food> food) {
        if (data == null)
            data = interactor.get(food);
        return data;
    }

    public void add(Id<Food> food, String eanCode) {
        interactor.add(EanNumberAddForm.create(food, eanCode));
    }
}
