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
import org.threeten.bp.Instant;

import javax.inject.Inject;
import java.util.List;

public class FoodItemRepository {

    private static final Logger LOG = new Logger(FoodItemRepository.class);

    private FoodItemDao foodItemDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public FoodItemRepository(FoodItemDao foodItemDao,
                              ServerClient webClient,
                              Synchroniser synchroniser) {
        this.foodItemDao = foodItemDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<FoodItemView>> getItemsOfType(int foodId) {
        LOG.d("getting food items of type " + foodId);
        return foodItemDao.getItemsOfType(foodId);
    }

    public LiveData<StatusCode> deleteItem(FoodItemView t) {
        LOG.d("deleting item " + t);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.deleteFoodItem(t.id, t.version)
                .enqueue(new StatusCodeCallback(result, synchroniser));
        return result;
    }

    public LiveData<FoodItemView> getItem(int id) {
        LOG.d("getting item with id " + id);
        return foodItemDao.getItem(id);
    }

    public LiveData<StatusCode> addItem(int foodId, int locationId, Instant eatBy) {
        LOG.d("adding item of type " + foodId + ", location " + locationId + ", eat by " + eatBy);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.addFoodItem(Config.DATABASE_DATE_FORMAT.format(eatBy), locationId, foodId)
                .enqueue(new StatusCodeCallback(result, synchroniser));
        return result;
    }

    public LiveData<Instant> getLatestExpirationOf(int foodId) {
        LOG.d("Getting latest expiration of " + foodId);
        return foodItemDao.getLatestExpirationOf(foodId);
    }

    public LiveData<StatusCode> editItem(int id, int version, int locationId, Instant eatBy) {
        LOG.d("editing item " + id);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        webClient.editFoodItem(id, version, Config.DATABASE_DATE_FORMAT.format(eatBy), locationId)
                .enqueue(new StatusCodeCallback(result, synchroniser));
        return result;
    }
}
