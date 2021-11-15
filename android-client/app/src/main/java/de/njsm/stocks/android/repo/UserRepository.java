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
import de.njsm.stocks.android.db.dao.UserDao;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.Principals;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class UserRepository {

    private static final Logger LOG = new Logger(UserRepository.class);

    private final UserDao userDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public UserRepository(UserDao userDao,
                          ServerClient webClient,
                          Synchroniser synchroniser,
                          IdlingResource idlingResource) {
        this.userDao = userDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<User>> getUsers() {
        LOG.d("getting users");
        return userDao.getAll();
    }

    public LiveData<User> getUser(int userId) {
        LOG.d("Getting user for id " + userId);
        return userDao.getUser(userId);
    }

    public LiveData<StatusCode> addUser(String name) {
        LOG.d("adding user " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        if (! Principals.isNameValid(name)) {
            data.setValue(StatusCode.INVALID_ARGUMENT);
            return data;
        }

        webClient.addUser(name)
                .enqueue(StatusCodeCallback.synchronise(data, idlingResource, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteUser(User entity) {
        LOG.d("deleting user " + entity);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteUser(entity.id, entity.version)
                .enqueue(StatusCodeCallback.synchronise(data, idlingResource, synchroniser));
        return data;
    }
}
