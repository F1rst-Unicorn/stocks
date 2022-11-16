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
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.UserDeviceListInteractor;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.business.entities.UserDevicesForListing;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class UserDeviceListViewModel extends ViewModel {

    private final UserDeviceListInteractor interactor;

    private final EntityDeleter<UserDevice> deleter;

    private final Synchroniser synchroniser;

    private Observable<UserDevicesForListing> data;

    @Inject
    UserDeviceListViewModel(UserDeviceListInteractor interactor, EntityDeleter<UserDevice> deleter, Synchroniser synchroniser) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.synchroniser = synchroniser;
    }

    public LiveData<UserDevicesForListing> get(Id<User> userId) {
        return LiveDataReactiveStreams.fromPublisher(
                getData(userId).toFlowable(BackpressureStrategy.LATEST));
    }

    private Observable<UserDevicesForListing> getData(Id<User> userId) {
        if (data == null)
            data = interactor.getData(userId);
        return data;
    }

    public void delete(int listItemIndex) {
        if (data == null)
            return;

        performOnCurrentData(list -> deleter.delete(list.devices().get(listItemIndex)));
    }

    private void performOnCurrentData(Consumer<UserDevicesForListing> runnable) {
        data.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }
}
