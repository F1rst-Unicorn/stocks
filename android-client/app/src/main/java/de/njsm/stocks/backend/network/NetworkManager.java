package de.njsm.stocks.backend.network;


import android.util.Log;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.network.tasks.*;
import de.njsm.stocks.common.data.*;

public class NetworkManager {

    private AsyncTaskFactory taskFactory;

    public NetworkManager(AsyncTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
        taskFactory.setNetworkManager(this);
    }

    public void synchroniseData() {
        Log.i(Config.LOG_TAG, "Synchronising data");
        SyncTask task = taskFactory.getSyncTask();
        task.execute();
    }

    public void synchroniseData(AsyncTaskCallback callback) {
        Log.i(Config.LOG_TAG, "Synchronising data with callback");
        SyncTask task = taskFactory.getSyncTask(callback);
        task.execute();
    }

    public void deleteDevice(UserDevice device) {
        Log.i(Config.LOG_TAG, "deleting device " + device);
        DeleteDeviceTask task = taskFactory.getDeleteDeviceTask();
        task.execute(device);
    }

    public void deleteFood(Food food) {
        Log.i(Config.LOG_TAG, "deleting food " + food);
        DeleteFoodTask task = taskFactory.getDeleteFoodTask();
        task.execute(food);
    }

    public void deleteFoodItem(FoodItem item) {
        Log.i(Config.LOG_TAG, "deleting item " + item);
        DeleteItemTask task = taskFactory.getDeleteItemTask();
        task.execute(item);
    }

    public void deleteLocation(Location location) {
        Log.i(Config.LOG_TAG, "deleting location " + location);
        DeleteLocationTask task = taskFactory.getDeleteLocationTask();
        task.execute(location);
    }

    public void deleteUser(User user) {
        Log.i(Config.LOG_TAG, "deleting user " + user);
        DeleteUserTask task = taskFactory.getDeleteUserTask();
        task.execute(user);
    }

    public void moveItem(FoodItem item, int locationId) {
        Log.i(Config.LOG_TAG, "moving item " + item + " to " + locationId);
        MoveItemTask task = taskFactory.getMoveItemTask();
        task.execute(item, locationId);
    }

    public void addDevice(String deviceName, int userId, TicketCallback callback) {
        Log.i(Config.LOG_TAG, "adding device with name " + deviceName);
        NewDeviceTask task = taskFactory.getNewDeviceTask(callback);
        task.execute(deviceName, userId);
    }

    public void addFoodItem(FoodItem item) {
        Log.i(Config.LOG_TAG, "adding item of type " + item.ofType);
        NewFoodItemTask task = taskFactory.getNewItemTask();
        task.execute(item);
    }

    public void addFood(Food food) {
        Log.i(Config.LOG_TAG, "adding food " + food);
        NewFoodTask task = taskFactory.getNewFoodTask();
        task.execute(food);
    }

    public void addLocation(Location location) {
        Log.i(Config.LOG_TAG, "adding location " + location);
        NewLocationTask task = taskFactory.getNewLocationTask();
        task.execute(location);
    }

    public void addUser(User user) {
        Log.i(Config.LOG_TAG, "adding user " + user);
        NewUserTask task = taskFactory.getNewUserTask();
        task.execute(user);
    }

    public void addEanNumber(EanNumber number) {
        Log.i(Config.LOG_TAG, "adding EAN number " + number);
        NewEanNumberTask task = taskFactory.getNewEanNumberTask();
        task.execute(number);
    }

    public void deleteEanNumber(EanNumber number) {
        Log.i(Config.LOG_TAG, "removing EAN number " + number);
        DeleteEanNumberTask task = taskFactory.getDeleteEanNumberTask();
        task.execute(number);
    }
}
