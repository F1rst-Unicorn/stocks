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
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.UserDeviceListInteractor;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.business.entities.UserDevicesForListing;

import javax.inject.Inject;
import java.util.function.Consumer;

public class UserDeviceListViewModel extends ViewModel {

    private final UserDeviceListInteractor interactor;

    private final EntityDeleter<UserDevice> deleter;

    private final Synchroniser synchroniser;

    private final ObservableDataCache<UserDevicesForListing> data;

    @Inject
    UserDeviceListViewModel(UserDeviceListInteractor interactor, EntityDeleter<UserDevice> deleter, Synchroniser synchroniser, ObservableDataCache<UserDevicesForListing> data) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<UserDevicesForListing> get(Id<User> userId) {
        return data.getLiveData(() -> interactor.getData(userId));
    }

    public void delete(int listItemIndex) {
        data.performOnNestedList(listItemIndex, UserDevicesForListing::devices, deleter::delete);
    }

    public void resolveId(int listItemIndex, Consumer<Id<UserDevice>> callback) {
        data.performOnNestedList(listItemIndex, UserDevicesForListing::devices, callback::accept);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
