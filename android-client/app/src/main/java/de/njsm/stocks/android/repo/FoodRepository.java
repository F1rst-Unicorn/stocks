package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.FoodDao;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

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
        webClient.renameFood(item.id, item.version, name)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<List<FoodView>> getFoodToEat() {
        return foodDao.getFoodToEat();
    }

    public LiveData<List<FoodView>> getFoodByLocation(int location) {
        return foodDao.getFoodByLocation(location);
    }

    public LiveData<Food> getFoodByEanNumber(String s) {
        return foodDao.getFoodByEanNumber(s);
    }

    public LiveData<List<FoodView>> getFoodBySubString(String searchTerm) {
        LOG.d("searching for %" + searchTerm + "%");
        return foodDao.getFoodBySubString("%" + searchTerm + "%");
    }

    public LiveData<List<Food>> getFood() {
        return foodDao.getFood();
    }
}
