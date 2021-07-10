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
import androidx.lifecycle.Transformations;
import de.njsm.stocks.android.db.dao.FoodDao;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodSummaryView;
import de.njsm.stocks.android.db.views.FoodSummaryWithExpirationView;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import org.threeten.bp.Instant;

import javax.inject.Inject;
import java.util.List;

public class FoodRepository {

    private static final Logger LOG = new Logger(FoodRepository.class);

    private final FoodDao foodDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public FoodRepository(FoodDao foodDao,
                          ServerClient webClient,
                          Synchroniser synchroniser,
                          IdlingResource idlingResource) {
        this.foodDao = foodDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<Food> getFood(int id) {
        LOG.d("getting food with id " + id);
        return foodDao.getFood(id);
    }

    public LiveData<List<FoodSummaryView.SingleFoodSummaryView>> getEmptyFood() {
        LOG.d("getting empty food");
        return foodDao.getEmptyFood();
    }

    public LiveData<StatusCode> addFood(String name) {
        LOG.d("adding food " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addFood(name)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> deleteFood(Food item) {
        LOG.d("deleting food " + item);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteFood(item.id, item.version)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> editFood(Food item) {
        LOG.d("editing food " + item);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.editFood(item.id, item.version, item.name, item.expirationOffset, item.location, item.description, item.storeUnit)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> editToBuyStatus(Food item, boolean toBuy) {
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        if (item.toBuy != toBuy) {
            LOG.d("setting " + item.name + "'s buy status to " + toBuy);
            webClient.setToBuyStatus(item.id, item.version, toBuy ? 1 : 0)
                    .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        } else {
            data.setValue(StatusCode.SUCCESS);
        }
        return data;
    }

    public LiveData<List<FoodSummaryWithExpirationView>> getFoodToEat() {
        return Transformations.map(foodDao.getFoodToEatSummary(), new FoodSummaryWithExpirationView.Mapper());
    }

    public LiveData<List<FoodSummaryWithExpirationView>> getFoodByLocationSummary(int location) {
        return Transformations.map(foodDao.getFoodByLocationSummary(location), new FoodSummaryWithExpirationView.Mapper());
    }

    public LiveData<Food> getFoodByEanNumber(String s) {
        return foodDao.getFoodByEanNumber(s);
    }

    public LiveData<List<FoodSummaryView>> getFoodToBuy() {
        return Transformations.map(foodDao.getFoodToBuy(), new FoodSummaryView.Mapper());
    }

    public LiveData<List<FoodSummaryView>> getFoodBySubString(String searchTerm) {
        LOG.d("searching for %" + searchTerm + "%");
        return Transformations.map(foodDao.getFoodBySubString("%" + searchTerm + "%"), new FoodSummaryView.Mapper());
    }

    public LiveData<List<Food>> getFood() {
        return foodDao.getAll();
    }

    public LiveData<Food> getFoodNowAsKnownBy(int id, Instant transactionTimeStart) {
        return foodDao.getFoodNowAsKnownBy(id, transactionTimeStart);
    }
}
