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
import java.util.Arrays;
import java.util.List;
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

    private final ScaledUnitDao scaledUnitDao;

    private final RecipeDao recipeDao;

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
                 ScaledUnitDao scaledUnitDao,
                 RecipeDao recipeDao,
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
        this.scaledUnitDao = scaledUnitDao;
        this.recipeDao = recipeDao;
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
        List<UpdateChain<? extends VersionedData>> entities = Arrays.asList(
                UpdateChain.of(serverClient.getUsers(1, null), userDao),
                UpdateChain.of(serverClient.getDevices(1, null), userDeviceDao),
                UpdateChain.of(serverClient.getLocations(1, null), locationDao),
                UpdateChain.of(serverClient.getFood(1, null), foodDao),
                UpdateChain.of(serverClient.getFoodItems(1, null), foodItemDao),
                UpdateChain.of(serverClient.getEanNumbers(1, null), eanNumberDao),
                UpdateChain.of(serverClient.getUnits(1, null), unitDao),
                UpdateChain.of(serverClient.getScaledUnits(1, null), scaledUnitDao),
                UpdateChain.of(serverClient.getRecipes(1, null), recipeDao)
        );

        for (UpdateChain<? extends VersionedData> chain : entities) {
            chain.refreshFully();
        }
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
            UpdateChain.of(serverClient.getUsers(1, rawLocalUpdate), userDao).refresh();
        } else if (table.equalsIgnoreCase("User_device")) {
            UpdateChain.of(serverClient.getDevices(1, rawLocalUpdate), userDeviceDao).refresh();
        } else if (table.equalsIgnoreCase("Location")) {
            UpdateChain.of(serverClient.getLocations(1, rawLocalUpdate), locationDao).refresh();
        } else if (table.equalsIgnoreCase("Food")) {
            UpdateChain.of(serverClient.getFood(1, rawLocalUpdate), foodDao).refresh();
        } else if (table.equalsIgnoreCase("Food_item")) {
            UpdateChain.of(serverClient.getFoodItems(1, rawLocalUpdate), foodItemDao).refresh();
        } else if (table.equalsIgnoreCase("EAN_number")) {
            UpdateChain.of(serverClient.getEanNumbers(1, rawLocalUpdate), eanNumberDao).refresh();
        } else if (table.equalsIgnoreCase("unit")) {
            UpdateChain.of(serverClient.getUnits(1, rawLocalUpdate), unitDao).refresh();
        } else if (table.equalsIgnoreCase("scaled_unit")) {
            UpdateChain.of(serverClient.getScaledUnits(1, rawLocalUpdate), scaledUnitDao).refresh();
        } else if (table.equalsIgnoreCase("recipe")) {
            UpdateChain.of(serverClient.getRecipes(1, rawLocalUpdate), recipeDao).refresh();
        }
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

    private static class UpdateChain<T> {

        private final Call<ListResponse<T>> call;

        private final Inserter<T> dao;

        static <T> UpdateChain<T> of(Call<ListResponse<T>> call, Inserter<T> data) {
            return new UpdateChain<>(call, data);
        }

        public UpdateChain(Call<ListResponse<T>> call, Inserter<T> dao) {
            this.call = call;
            this.dao = dao;
        }

        private void refreshFully() throws StatusCodeException {
            T[] data = StatusCodeCallback.executeCall(call);
            dao.synchronise(data);
        }

        private void refresh() throws StatusCodeException {
            T[] data = StatusCodeCallback.executeCall(call);
            dao.insert(data);
        }
    }
}
