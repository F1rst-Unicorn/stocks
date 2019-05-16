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
import de.njsm.stocks.android.util.Logger;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class Synchroniser {

    private static final Logger LOG = new Logger(Synchroniser.class);

    private ServerClient serverClient;

    private UserDao userDao;

    private UserDeviceDao userDeviceDao;

    private LocationDao locationDao;

    private FoodDao foodDao;

    private FoodItemDao foodItemDao;

    private EanNumberDao eanNumberDao;

    private UpdateDao updateDao;

    private Executor executor;

    @Inject
    Synchroniser(ServerClient serverClient,
                 UserDao userDao,
                 UserDeviceDao userDeviceDao,
                 LocationDao locationDao,
                 FoodDao foodDao,
                 FoodItemDao foodItemDao,
                 EanNumberDao eanNumberDao,
                 UpdateDao updateDao,
                 Executor executor) {
        this.serverClient = serverClient;
        this.userDao = userDao;
        this.userDeviceDao = userDeviceDao;
        this.locationDao = locationDao;
        this.foodDao = foodDao;
        this.foodItemDao = foodItemDao;
        this.eanNumberDao = eanNumberDao;
        this.updateDao = updateDao;
        this.executor = executor;
    }

    public LiveData<StatusCode> synchroniseFully() {
        return synchronise(true);
    }

    public LiveData<StatusCode> synchronise() {
        return synchronise(false);
    }

    private LiveData<StatusCode> synchronise(boolean full) {
        MutableLiveData<StatusCode> result = new MutableLiveData<>();
        LOG.i("Starting" + (full ? " full " : " ") + "synchronisation");
        executor.execute(() -> {

            LOG.d("Synchronising");
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
        });
        return result;
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
    }

    void refreshOutdatedTables(Update[] serverUpdates, Update[] localUpdates) throws StatusCodeException {
        for (Update update : serverUpdates) {
            if (isTableOutdated(localUpdates, update)) {
                LOG.d("Refreshing " + update.table);
                refresh(update.table);
            } else
                LOG.v("Table " + update.table + " is up to date");
        }
    }

    private void refresh(String table) throws StatusCodeException {
        if (table.equals("User")) {
            refreshUsers();
        } else if (table.equals("User_device")) {
            refreshUserDevices();
        } else if (table.equals("Location")) {
            refreshLocations();
        } else if (table.equals("Food")) {
            refreshFood();
        } else if (table.equals("Food_item")) {
            refreshFoodItems();
        } else if (table.equals("Ean_number")) {
            refreshEanNumbers();
        }
    }

    private void refreshUsers() throws StatusCodeException {
        User[] u = StatusCodeCallback.executeCall(serverClient.getUsers());
        userDao.synchronise(u);
    }

    private void refreshLocations() throws StatusCodeException {
        Location[] u = StatusCodeCallback.executeCall(serverClient.getLocations());
        locationDao.synchronise(u);
    }

    private void refreshUserDevices() throws StatusCodeException {
        UserDevice[] u = StatusCodeCallback.executeCall(serverClient.getDevices());
        userDeviceDao.synchronise(u);
    }

    private void refreshFood() throws StatusCodeException {
        Food[] u = StatusCodeCallback.executeCall(serverClient.getFood());
        foodDao.synchronise(u);
    }

    private void refreshFoodItems() throws StatusCodeException {
        FoodItem[] u = StatusCodeCallback.executeCall(serverClient.getFoodItems());
        foodItemDao.synchronise(u);
    }

    private void refreshEanNumbers() throws StatusCodeException {
        EanNumber[] u = StatusCodeCallback.executeCall(serverClient.getEanNumbers());
        eanNumberDao.synchronise(u);
    }

    private boolean isTableOutdated(Update[] localUpdates, Update update) {
        Update localUpdate = getLocalUpdate(localUpdates, update.table);
        return localUpdate != null && update.lastUpdate.isAfter(localUpdate.lastUpdate);
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
