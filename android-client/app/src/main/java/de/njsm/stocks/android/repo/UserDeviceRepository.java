/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import javax.inject.Inject;

import de.njsm.stocks.android.db.dao.UserDeviceDao;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.frontend.device.ServerTicket;
import de.njsm.stocks.android.network.server.DataResultCallback;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.Principals;
import fj.data.Validation;

public class UserDeviceRepository {

    private static final Logger LOG = new Logger(UserDeviceRepository.class);

    private UserDeviceDao userDeviceDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public UserDeviceRepository(UserDeviceDao userDeviceDao,
                                ServerClient webClient,
                                Synchroniser synchroniser) {
        this.userDeviceDao = userDeviceDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<UserDevice>> getUserDevices(int userId) {
        LOG.d("getting user devices of " + userId);
        return userDeviceDao.getDevicesOfUser(userId);
    }

    public LiveData<Validation<StatusCode, ServerTicket>> addUserDevice(String name, int userId) {
        LOG.d("adding user device" + name);
        MediatorLiveData<Validation<StatusCode, ServerTicket>> data = new MediatorLiveData<>();

        if (! Principals.isNameValid(name)) {
            data.setValue(Validation.fail(StatusCode.INVALID_ARGUMENT));
            return data;
        }

        webClient.addDevice(name, userId)
                .enqueue(new DataResultCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteUserDevice(UserDevice entity) {
        LOG.d("deleting user device" + entity);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteDevice(entity.id, entity.version)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }
}
