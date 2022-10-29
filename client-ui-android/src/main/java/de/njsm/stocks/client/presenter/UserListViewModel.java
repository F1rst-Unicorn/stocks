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
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.UserListInteractor;
import de.njsm.stocks.client.business.entities.UserForListing;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class UserListViewModel extends ViewModel {

    private final UserListInteractor userListInteractor;

    private final Synchroniser synchroniser;

    private Observable<List<UserForListing>> data;

    @Inject
    UserListViewModel(UserListInteractor userListInteractor, Synchroniser synchroniser) {
        this.userListInteractor = userListInteractor;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<UserForListing>> get() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST));
    }

    public void delete(int listItemIndex) {
        throw new UnsupportedOperationException("TODO");
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public void resolveId(int listItemIndex, Consumer<Integer> callback) {
        performOnCurrentLocations(list -> callback.accept(list.get(listItemIndex).id()));
    }

    private void performOnCurrentLocations(Consumer<List<UserForListing>> runnable) {
        getData()
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    private Observable<List<UserForListing>> getData() {
        if (data == null)
            data = userListInteractor.getUsers();
        return data;
    }
}
