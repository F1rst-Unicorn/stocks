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

import de.njsm.stocks.android.db.dao.FoodDao;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodWithLatestItemView;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;

public class FoodRepository {

    private static final Logger LOG = new Logger(FoodRepository.class);

    private FoodDao foodDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public FoodRepository(FoodDao foodDao,
                          ServerClient webClient,
                          Synchroniser synchroniser) {
        this.foodDao = foodDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
    }

    public LiveData<Food> getFood(int id) {
        LOG.d("getting food with id " + id);
        return foodDao.getFood(id);
    }

    public LiveData<List<Food>> getEmptyFood() {
        LOG.d("getting empty food");
        return foodDao.getEmptyFood();
    }

    public LiveData<StatusCode> addFood(String name) {
        LOG.d("adding food " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addFood(name)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteFood(Food item) {
        LOG.d("deleting food " + item);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteFood(item.id, item.version)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> renameFood(Food item, String name) {
        LOG.d("renaming food " + item + " to " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.renameFood(item.id, item.version, name, item.expirationOffset, item.location)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> editToBuyStatus(Food item, boolean toBuy) {
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        if (item.toBuy != toBuy) {
            LOG.d("setting " + item.name + "'s buy status to " + toBuy);
            webClient.setToBuyStatus(item.id, item.version, toBuy ? 1 : 0)
                    .enqueue(new StatusCodeCallback(data, synchroniser));
        } else {
            data.setValue(StatusCode.SUCCESS);
        }
        return data;
    }

    public LiveData<StatusCode> setFoodExpirationOffset(Food item, int newOffset) {
        LOG.d("setting food expiration offset of " + item + " to " + newOffset);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.renameFood(item.id, item.version, item.name, newOffset, item.location)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;

    }

    public LiveData<StatusCode> setFoodDefaultLocation(Food item, int location) {
        LOG.d("setting food default of " + item + " to " + location);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.renameFood(item.id, item.version, item.name, item.expirationOffset, location)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;

    }

    public LiveData<List<FoodWithLatestItemView>> getFoodToEat() {
        return foodDao.getFoodToEat();
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodByLocation(int location) {
        return foodDao.getFoodByLocation(location);
    }

    public LiveData<Food> getFoodByEanNumber(String s) {
        return foodDao.getFoodByEanNumber(s);
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodToBuy() {
        return foodDao.getFoodToBuy();
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodBySubString(String searchTerm) {
        LOG.d("searching for %" + searchTerm + "%");
        return foodDao.getFoodBySubString("%" + searchTerm + "%");
    }

    public LiveData<List<Food>> getFood() {
        return foodDao.getAll();
    }
}
