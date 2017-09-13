package de.njsm.stocks.backend.network;

import android.content.ContextWrapper;
import de.njsm.stocks.backend.network.tasks.*;

public class AsyncTaskFactory {

    private ContextWrapper contextWrapper;

    private NetworkManager networkManager;

    private ServerManager serverManager;

    public AsyncTaskFactory(ContextWrapper contextWrapper) {
        this.contextWrapper = contextWrapper;
        this.serverManager = new ServerManager(contextWrapper);
    }

    public SyncTask getSyncTask() {
        return new SyncTask(contextWrapper.getFilesDir(),
                serverManager,
                contextWrapper.getContentResolver(),
                null);
    }

    public SyncTask getSyncTask(AsyncTaskCallback callback) {
        return new SyncTask(contextWrapper.getFilesDir(),
                serverManager,
                contextWrapper.getContentResolver(),
                callback);
    }
    
    public DeleteDeviceTask getDeleteDeviceTask() {
        return new DeleteDeviceTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public DeleteFoodTask getDeleteFoodTask() {
        return new DeleteFoodTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public DeleteItemTask getDeleteItemTask() {
        return new DeleteItemTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public DeleteLocationTask getDeleteLocationTask() {
        return new DeleteLocationTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public DeleteUserTask getDeleteUserTask() {
        return new DeleteUserTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public MoveItemTask getMoveItemTask() {
        return new MoveItemTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public NewDeviceTask getNewDeviceTask(NewDeviceTask.TicketCallback callback) {
        return new NewDeviceTask(contextWrapper.getFilesDir(), serverManager, networkManager, callback);
    }
    
    public NewFoodItemTask getNewItemTask() {
        return new NewFoodItemTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public NewFoodTask getNewFoodTask() {
        return new NewFoodTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public NewLocationTask getNewLocationTask() {
        return new NewLocationTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }
    
    public NewUserTask getNewUserTask() {
        return new NewUserTask(contextWrapper.getFilesDir(), serverManager, networkManager);
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
