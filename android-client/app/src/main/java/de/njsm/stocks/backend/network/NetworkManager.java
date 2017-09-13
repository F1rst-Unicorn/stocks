package de.njsm.stocks.backend.network;


import de.njsm.stocks.backend.network.tasks.*;
import de.njsm.stocks.common.data.*;

public class NetworkManager {

    private AsyncTaskFactory taskFactory;

    public NetworkManager(AsyncTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void synchroniseData() {
        SyncTask task = taskFactory.getSyncTask();
        task.execute();
    }

    public void synchroniseData(AsyncTaskCallback callback) {
        SyncTask task = taskFactory.getSyncTask(callback);
        task.execute();
    }

    public void deleteDevice(UserDevice device) {
        DeleteDeviceTask task = taskFactory.getDeleteDeviceTask();
        task.execute(device);
    }

    public void deleteFood(Food food) {
        DeleteFoodTask task = taskFactory.getDeleteFoodTask();
        task.execute(food);
    }

    public void deleteFoodItem(FoodItem item) {
        DeleteItemTask task = taskFactory.getDeleteItemTask();
        task.execute(item);
    }

    public void deleteLocation(Location location) {
        DeleteLocationTask task = taskFactory.getDeleteLocationTask();
        task.execute(location);
    }

    public void deleteUser(User user) {
        DeleteUserTask task = taskFactory.getDeleteUserTask();
        task.execute(user);
    }

    public void moveItem(FoodItem item, int locationId) {
        MoveItemTask task = taskFactory.getMoveItemTask();
        task.execute(item, locationId);
    }

    public void addDevice(String deviceName, int userId, NewDeviceTask.TicketCallback callback) {
        NewDeviceTask task = taskFactory.getNewDeviceTask(callback);
        task.execute(deviceName, userId);
    }

    public void addFoodItem(FoodItem item) {
        NewFoodItemTask task = taskFactory.getNewItemTask();
        task.execute(item);
    }

    public void addFood(Food food) {
        NewFoodTask task = taskFactory.getNewFoodTask();
        task.execute(food);
    }

    public void addLocation(Location location) {
        NewLocationTask task = taskFactory.getNewLocationTask();
        task.execute(location);
    }

    public void addUser(User user) {
        NewUserTask task = taskFactory.getNewUserTask();
        task.execute(user);
    }

}
