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
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.dao.FoodItemDao;
import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.db.views.ScaledAmount;
import de.njsm.stocks.android.frontend.recipecheckout.Adapter;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;

public class FoodItemRepository {

    private static final Logger LOG = new Logger(FoodItemRepository.class);

    private final FoodItemDao foodItemDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    private final Executor executor;

    @Inject
    public FoodItemRepository(FoodItemDao foodItemDao,
                              ServerClient webClient,
                              Synchroniser synchroniser,
                              IdlingResource idlingResource,
                              Executor executor) {
        this.foodItemDao = foodItemDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
        this.executor = executor;
    }

    public LiveData<List<FoodItemView>> getItemsOfType(int foodId) {
        LOG.d("getting food items of type " + foodId);
        return foodItemDao.getItemsOfType(foodId);
    }

    public LiveData<StatusCode> deleteItem(FoodItemView t) {
        LOG.d("deleting item " + t);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.deleteFoodItem(t.id, t.version)
                .enqueue(StatusCodeCallback.synchronise(result, idlingResource, synchroniser));
        return result;
    }

    public LiveData<FoodItemView> getItem(int id) {
        LOG.d("getting item with id " + id);
        return foodItemDao.getItem(id);
    }

    public LiveData<StatusCode> addItem(int foodId, int locationId, Instant eatBy, int unit) {
        LOG.d("adding item of type " + foodId + ", location " + locationId + ", eat by " + eatBy);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.addFoodItem(Config.API_DATE_FORMAT.format(eatBy), locationId, foodId, unit)
                .enqueue(StatusCodeCallback.synchronise(result, idlingResource, synchroniser));
        return result;
    }

    public LiveData<Instant> getLatestExpirationOf(int foodId) {
        LOG.d("Getting latest expiration of " + foodId);
        return foodItemDao.getLatestExpirationOf(foodId);
    }

    public LiveData<StatusCode> editItem(FoodItemView item) {
        LOG.d("editing " + item);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.editFoodItem(
                item.getId(),
                item.getVersion(),
                Config.API_DATE_FORMAT.format(item.getEatByDate()),
                item.getStoredIn(),
                item.getUnit())
                .enqueue(StatusCodeCallback.synchronise(result, idlingResource, synchroniser));
        return result;
    }

    public LiveData<List<ScaledAmount>> countItemsOfType(int foodId) {
        LOG.d("counting items of type " + foodId);
        return foodItemDao.countItemsOfType(foodId);
    }

    public LiveData<FoodItemView> getNowAsKnownBy(int id, Instant transactionTime) {
        return foodItemDao.getNowAsKnownBy(id, transactionTime);
    }


    public void checkoutFood(List<Adapter.FormDataItem> foodToCheckOut) {
        executor.execute(() -> {
            synchroniser.synchroniseInThread(false, new MutableLiveData<>());

            for (Adapter.FormDataItem food : foodToCheckOut) {
                List<FoodItem> itemsToCheckout = foodItemDao.findItems(food.getFoodId(), food.getScaledUnitId(), food.getAmount());

                for (FoodItem item : itemsToCheckout) {
                    LOG.d("deleting item " + item);
                    webClient.deleteFoodItem(item.getId(), item.getVersion())
                            .enqueue(StatusCodeCallback.synchronise(new MediatorLiveData<>(), idlingResource, synchroniser));
                }
            }
        });
    }
}
