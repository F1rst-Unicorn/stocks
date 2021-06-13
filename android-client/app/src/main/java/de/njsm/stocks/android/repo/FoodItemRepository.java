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
import de.njsm.stocks.android.db.dao.FoodItemDao;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import org.threeten.bp.Instant;

import javax.inject.Inject;
import java.util.List;

public class FoodItemRepository {

    private static final Logger LOG = new Logger(FoodItemRepository.class);

    private final FoodItemDao foodItemDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public FoodItemRepository(FoodItemDao foodItemDao,
                              ServerClient webClient,
                              Synchroniser synchroniser,
                              IdlingResource idlingResource) {
        this.foodItemDao = foodItemDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<FoodItemView>> getItemsOfType(int foodId) {
        LOG.d("getting food items of type " + foodId);
        return foodItemDao.getItemsOfType(foodId);
    }

    public LiveData<StatusCode> deleteItem(FoodItemView t) {
        LOG.d("deleting item " + t);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.deleteFoodItem(t.id, t.version)
                .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
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
                .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
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
                .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
        return result;
    }

    public LiveData<Integer> countItemsOfType(int foodId) {
        LOG.d("editing items of type " + foodId);
        return foodItemDao.countItemsOfType(foodId);
    }

    public LiveData<FoodItemView> getNowAsKnownBy(int id, Instant transactionTime) {
        return foodItemDao.getNowAsKnownBy(id, transactionTime);
    }
}
