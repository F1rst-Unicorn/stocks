package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.network.tasks.*;

public class AsyncTaskFactory {

    private ContextWrapper contextWrapper;

    private NetworkManager networkManager;

    public AsyncTaskFactory(ContextWrapper contextWrapper) {
        this.contextWrapper = contextWrapper;
    }

    public SyncTask getSyncTask() {
        return new SyncTask(contextWrapper.getFilesDir(),
                contextWrapper.getContentResolver(),
                null);
    }

    public SyncTask getSyncTask(AsyncTaskCallback callback) {
        return new SyncTask(contextWrapper.getFilesDir(),
                contextWrapper.getContentResolver(),
                callback);
    }
    
    public DeleteDeviceTask getDeleteDeviceTask() {
        return new DeleteDeviceTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public DeleteFoodTask getDeleteFoodTask() {
        return new DeleteFoodTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public DeleteItemTask getDeleteItemTask() {
        return new DeleteItemTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public DeleteLocationTask getDeleteLocationTask() {
        return new DeleteLocationTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public DeleteUserTask getDeleteUserTask() {
        return new DeleteUserTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public MoveItemTask getMoveItemTask() {
        return new MoveItemTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public NewDeviceTask getNewDeviceTask(NewDeviceTask.TicketCallback callback) {
        return new NewDeviceTask(contextWrapper.getFilesDir(), networkManager, callback);
    }
    
    public NewFoodItemTask getNewItemTask() {
        return new NewFoodItemTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public NewFoodTask getNewFoodTask() {
        return new NewFoodTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public NewLocationTask getNewLocationTask() {
        return new NewLocationTask(contextWrapper.getFilesDir(), networkManager);
    }
    
    public NewUserTask getNewUserTask() {
        return new NewUserTask(contextWrapper.getFilesDir(), networkManager);
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
