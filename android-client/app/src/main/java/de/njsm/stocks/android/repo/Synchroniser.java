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
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.entities.*;
import de.njsm.stocks.android.error.StatusCodeException;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.network.server.data.ListResponse;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import org.threeten.bp.Instant;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class Synchroniser {

    private static final Logger LOG = new Logger(Synchroniser.class);

    private final ServerClient serverClient;

    private final UserDao userDao;

    private final UserDeviceDao userDeviceDao;

    private final LocationDao locationDao;

    private final FoodDao foodDao;

    private final FoodItemDao foodItemDao;

    private final EanNumberDao eanNumberDao;

    private final UnitDao unitDao;

    private final UpdateDao updateDao;

    private final Executor executor;

    private final IdlingResource idlingResource;

    @Inject
    Synchroniser(ServerClient serverClient,
                 UserDao userDao,
                 UserDeviceDao userDeviceDao,
                 LocationDao locationDao,
                 FoodDao foodDao,
                 FoodItemDao foodItemDao,
                 EanNumberDao eanNumberDao,
                 UnitDao unitDao,
                 UpdateDao updateDao,
                 Executor executor,
                 IdlingResource idlingResource) {
        this.serverClient = serverClient;
        this.userDao = userDao;
        this.userDeviceDao = userDeviceDao;
        this.locationDao = locationDao;
        this.foodDao = foodDao;
        this.foodItemDao = foodItemDao;
        this.eanNumberDao = eanNumberDao;
        this.unitDao = unitDao;
        this.updateDao = updateDao;
        this.executor = executor;
        this.idlingResource = idlingResource;
    }

    public LiveData<StatusCode> synchroniseFully() {
        return synchronise(true);
    }

    public LiveData<StatusCode> synchronise() {
        return synchronise(false);
    }

    private LiveData<StatusCode> synchronise(boolean full) {
        MutableLiveData<StatusCode> result = new MutableLiveData<>();
        idlingResource.increment();
        executor.execute(() -> synchroniseInThread(full, result));
        return result;
    }

    void synchroniseInThread(boolean full, MutableLiveData<StatusCode> result) {
        LOG.i("Starting" + (full ? " full " : " ") + "synchronisation");

        if (full)
            updateDao.reset();

        try {
            Call<ListResponse<Update>> call = serverClient.getUpdates();
            Update[] serverUpdates = StatusCodeCallback.executeCall(call);
            enumerateServerUpdates(serverUpdates);
            Update[] localUpdates = updateDao.getAll();
            updateTables(serverUpdates, localUpdates);
            result.postValue(StatusCode.SUCCESS);
        } catch (StatusCodeException e) {
            result.postValue(e.getCode());
        }
        idlingResource.decrement();
    }

    // Room needs primary keys on entities, but server doesn't provide IDs here
    private void enumerateServerUpdates(Update[] serverUpdates) {
        int i = 1;
        for (Update u : serverUpdates) {
            u.id = i++;
        }
    }

    private void updateTables(Update[] serverUpdates, Update[] localUpdates) throws StatusCodeException {
        if (serverUpdates.length == 0) {
            LOG.e("Server updates are empty");
        } else if (localUpdates.length == 0) {
            LOG.d("Updating all tables as local updates are empty");
            refreshAll();
        } else {
            refreshOutdatedTables(serverUpdates, localUpdates);
        }
        updateDao.set(serverUpdates);
    }

    private void refreshAll() throws StatusCodeException {
        refreshUsers();
        refreshUserDevices();
        refreshLocations();
        refreshFood();
        refreshFoodItems();
        refreshEanNumbers();
        refreshUnits();
    }

    void refreshOutdatedTables(Update[] serverUpdates, Update[] localUpdates) throws StatusCodeException {
        for (Update update : serverUpdates) {
            Instant localUpdate = getLatestLocalUpdate(localUpdates, update);
            if (localUpdate != null) {
                if (localUpdate.isBefore(update.lastUpdate)) {
                    LOG.d("Refreshing " + update.table + " starting from " + Config.API_DATE_FORMAT.format(localUpdate));
                    refresh(update.table, localUpdate);
                }
            } else
                LOG.v("Table " + update.table + " not found");
        }
    }

    private void refresh(String table, Instant localUpdate) throws StatusCodeException {
        String rawLocalUpdate = Config.API_DATE_FORMAT.format(localUpdate);
        if (table.equalsIgnoreCase("User")) {
            refreshUsers(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("User_device")) {
            refreshUserDevices(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("Location")) {
            refreshLocations(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("Food")) {
            refreshFood(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("Food_item")) {
            refreshFoodItems(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("EAN_number")) {
            refreshEanNumbers(rawLocalUpdate);
        } else if (table.equalsIgnoreCase("unit")) {
            refreshUnits(rawLocalUpdate);
        }
    }

    private void refreshUsers() throws StatusCodeException {
        User[] u = StatusCodeCallback.executeCall(serverClient.getUsers(1, null));
        userDao.synchronise(u);
    }

    private void refreshLocations() throws StatusCodeException {
        Location[] u = StatusCodeCallback.executeCall(serverClient.getLocations(1, null));
        locationDao.synchronise(u);
    }

    private void refreshUserDevices() throws StatusCodeException {
        UserDevice[] u = StatusCodeCallback.executeCall(serverClient.getDevices(1, null));
        userDeviceDao.synchronise(u);
    }

    private void refreshFood() throws StatusCodeException {
        Food[] u = StatusCodeCallback.executeCall(serverClient.getFood(1, null));
        foodDao.synchronise(u);
    }

    private void refreshFoodItems() throws StatusCodeException {
        FoodItem[] u = StatusCodeCallback.executeCall(serverClient.getFoodItems(1, null));
        foodItemDao.synchronise(u);
    }

    private void refreshEanNumbers() throws StatusCodeException {
        EanNumber[] u = StatusCodeCallback.executeCall(serverClient.getEanNumbers(1, null));
        eanNumberDao.synchronise(u);
    }

    private void refreshUnits() throws StatusCodeException {
        Unit[] u = StatusCodeCallback.executeCall(serverClient.getUnits(1, null));
        unitDao.synchronise(u);
    }

    private void refreshUsers(String startingFrom) throws StatusCodeException {
        User[] u = StatusCodeCallback.executeCall(serverClient.getUsers(1, startingFrom));
        userDao.insert(u);
    }

    private void refreshLocations(String startingFrom) throws StatusCodeException {
        Location[] u = StatusCodeCallback.executeCall(serverClient.getLocations(1, startingFrom));
        locationDao.insert(u);
    }

    private void refreshUserDevices(String startingFrom) throws StatusCodeException {
        UserDevice[] u = StatusCodeCallback.executeCall(serverClient.getDevices(1, startingFrom));
        userDeviceDao.insert(u);
    }

    private void refreshFood(String startingFrom) throws StatusCodeException {
        Food[] u = StatusCodeCallback.executeCall(serverClient.getFood(1, startingFrom));
        foodDao.insert(u);
    }

    private void refreshFoodItems(String startingFrom) throws StatusCodeException {
        FoodItem[] u = StatusCodeCallback.executeCall(serverClient.getFoodItems(1, startingFrom));
        foodItemDao.insert(u);
    }

    private void refreshEanNumbers(String startingFrom) throws StatusCodeException {
        EanNumber[] u = StatusCodeCallback.executeCall(serverClient.getEanNumbers(1, startingFrom));
        eanNumberDao.insert(u);
    }

    private void refreshUnits(String startingFrom) throws StatusCodeException {
        Unit[] u = StatusCodeCallback.executeCall(serverClient.getUnits(1, startingFrom));
        unitDao.insert(u);
    }

    private Instant getLatestLocalUpdate(Update[] localUpdates, Update update) {
        Update localUpdate = getLocalUpdate(localUpdates, update.table);
        return localUpdate != null ? localUpdate.lastUpdate : null;
    }

    private Update getLocalUpdate(Update[] localUpdates, String table) {
        for (Update u : localUpdates) {
            if (u.table.equals(table)) {
                return u;
            }
        }
        return null;
    }
}
