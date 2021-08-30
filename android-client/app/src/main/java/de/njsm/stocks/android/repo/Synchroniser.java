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
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.db.entities.VersionedData;
import de.njsm.stocks.android.error.StatusCodeException;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.Bitemporal;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.StatusCode;
import retrofit2.Call;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final RecipeIngredientDao recipeIngredientDao;

    private final RecipeProductDao recipeProductDao;

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
                 RecipeIngredientDao recipeIngredientDao,
                 RecipeProductDao recipeProductDao,
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
        this.recipeIngredientDao = recipeIngredientDao;
        this.recipeProductDao = recipeProductDao;
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
            Call<ListResponse<de.njsm.stocks.common.api.Update>> call = serverClient.getUpdates();
            List<de.njsm.stocks.common.api.Update> networkServerUpdates = StatusCodeCallback.executeCall(call);
            List<Update> serverUpdates = networkServerUpdates.stream()
                    .map(v -> new Update(0, v.table(), v.lastUpdate()))
                    .collect(Collectors.toList());
            enumerateServerUpdates(serverUpdates);
            List<Update> localUpdates = updateDao.getAll();
            updateTables(serverUpdates, localUpdates);
            result.postValue(StatusCode.SUCCESS);
        } catch (StatusCodeException e) {
            result.postValue(e.getCode());
        }
        idlingResource.decrement();
    }

    // Room needs primary keys on entities, but server doesn't provide IDs here
    private void enumerateServerUpdates(List<Update> serverUpdates) {
        int i = 1;
        for (Update u : serverUpdates) {
            u.id = i++;
        }
    }

    private void updateTables(List<Update> serverUpdates, List<Update> localUpdates) throws StatusCodeException {
        if (serverUpdates.size() == 0) {
            LOG.e("Server updates are empty");
        } else if (localUpdates.size() == 0) {
            LOG.d("Updating all tables as local updates are empty");
            refreshAll();
        } else {
            refreshOutdatedTables(serverUpdates, localUpdates);
        }
        updateDao.set(serverUpdates);
    }

    private void refreshAll() throws StatusCodeException {
        BitemporalEntityMapperVisitor mapper = new BitemporalEntityMapperVisitor();
        List<UpdateChain<?, ?, ?>> entities = Arrays.asList(
                UpdateChain.of(serverClient.getUsers(null), userDao, mapper::bitemporalUser),
                UpdateChain.of(serverClient.getDevices(null), userDeviceDao, mapper::bitemporalUserDevice),
                UpdateChain.of(serverClient.getLocations(null), locationDao, mapper::bitemporalLocation),
                UpdateChain.of(serverClient.getFood(null), foodDao, mapper::bitemporalFood),
                UpdateChain.of(serverClient.getFoodItems(null), foodItemDao, mapper::bitemporalFoodItem),
                UpdateChain.of(serverClient.getEanNumbers(null), eanNumberDao, mapper::bitemporalEanNumber),
                UpdateChain.of(serverClient.getUnits(null), unitDao, mapper::bitemporalUnit),
                UpdateChain.of(serverClient.getScaledUnits(null), scaledUnitDao, mapper::bitemporalScaledUnit),
                UpdateChain.of(serverClient.getRecipes(null), recipeDao, mapper::bitemporalRecipe),
                UpdateChain.of(serverClient.getRecipeIngredients(null), recipeIngredientDao, mapper::bitemporalRecipeIngredient),
                UpdateChain.of(serverClient.getRecipeProducts(null), recipeProductDao, mapper::bitemporalRecipeProduct)
        );

        for (UpdateChain<?, ?, ?> chain : entities) {
            chain.refreshFully();
        }
    }

    void refreshOutdatedTables(List<Update> serverUpdates, List<Update> localUpdates) throws StatusCodeException {
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
        BitemporalEntityMapperVisitor mapper = new BitemporalEntityMapperVisitor();
        if (table.equalsIgnoreCase("User")) {
            UpdateChain.of(serverClient.getUsers(rawLocalUpdate), userDao, mapper::bitemporalUser).refresh();
        } else if (table.equalsIgnoreCase("User_device")) {
            UpdateChain.of(serverClient.getDevices(rawLocalUpdate), userDeviceDao, mapper::bitemporalUserDevice).refresh();
        } else if (table.equalsIgnoreCase("Location")) {
            UpdateChain.of(serverClient.getLocations(rawLocalUpdate), locationDao, mapper::bitemporalLocation).refresh();
        } else if (table.equalsIgnoreCase("Food")) {
            UpdateChain.of(serverClient.getFood(rawLocalUpdate), foodDao, mapper::bitemporalFood).refresh();
        } else if (table.equalsIgnoreCase("Food_item")) {
            UpdateChain.of(serverClient.getFoodItems(rawLocalUpdate), foodItemDao, mapper::bitemporalFoodItem).refresh();
        } else if (table.equalsIgnoreCase("EAN_number")) {
            UpdateChain.of(serverClient.getEanNumbers(rawLocalUpdate), eanNumberDao, mapper::bitemporalEanNumber).refresh();
        } else if (table.equalsIgnoreCase("unit")) {
            UpdateChain.of(serverClient.getUnits(rawLocalUpdate), unitDao, mapper::bitemporalUnit).refresh();
        } else if (table.equalsIgnoreCase("scaled_unit")) {
            UpdateChain.of(serverClient.getScaledUnits(rawLocalUpdate), scaledUnitDao, mapper::bitemporalScaledUnit).refresh();
        } else if (table.equalsIgnoreCase("recipe")) {
            UpdateChain.of(serverClient.getRecipes(rawLocalUpdate), recipeDao, mapper::bitemporalRecipe).refresh();
        } else if (table.equalsIgnoreCase("recipe_ingredient")) {
            UpdateChain.of(serverClient.getRecipeIngredients(rawLocalUpdate), recipeIngredientDao, mapper::bitemporalRecipeIngredient).refresh();
        } else if (table.equalsIgnoreCase("recipe_product")) {
            UpdateChain.of(serverClient.getRecipeProducts(rawLocalUpdate), recipeProductDao, mapper::bitemporalRecipeProduct).refresh();
        }
    }

    private Instant getLatestLocalUpdate(List<Update> localUpdates, Update update) {
        Update localUpdate = getLocalUpdate(localUpdates, update.table);
        return localUpdate != null ? localUpdate.lastUpdate : null;
    }

    private Update getLocalUpdate(List<Update> localUpdates, String table) {
        for (Update u : localUpdates) {
            if (u.table.equalsIgnoreCase(table)) {
                return u;
            }
        }
        return null;
    }

    private static class UpdateChain<N extends Entity<N>, T extends Bitemporal<N>, E extends VersionedData> {

        private final Call<ListResponse<T>> call;

        private final Inserter<E> dao;

        private final Function<T, E> mapper;

        static <N extends Entity<N>, T extends Bitemporal<N>, E extends VersionedData> UpdateChain<N, T, E> of(Call<ListResponse<T>> call,
                                                                                                               Inserter<E> data,
                                                                                                               Function<T, E> mapper) {
            return new UpdateChain<>(call, data, mapper);
        }

        public UpdateChain(Call<ListResponse<T>> call, Inserter<E> dao, Function<T, E> mapper) {
            this.call = call;
            this.dao = dao;
            this.mapper = mapper;
        }

        private void refreshFully() throws StatusCodeException {
            List<E> data = StatusCodeCallback.executeCall(call).stream()
                    .map(mapper)
                    .collect(Collectors.toList());
            dao.synchronise(data);
        }

        private void refresh() throws StatusCodeException {
            List<E> data = StatusCodeCallback.executeCall(call).stream()
                    .map(mapper)
                    .collect(Collectors.toList());
            dao.insert(data);
        }
    }
}
