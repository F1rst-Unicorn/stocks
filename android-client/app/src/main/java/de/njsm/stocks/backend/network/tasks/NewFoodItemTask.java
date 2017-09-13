package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.FoodItem;

import java.io.File;

public class NewFoodItemTask extends AbstractAsyncTask<FoodItem, Void, Integer> {

    private NetworkManager networkManager;

    public NewFoodItemTask(File exceptionFileDirectory, NetworkManager networkManager) {
        super(exceptionFileDirectory);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(FoodItem... params) {
        ServerManager.m.addItem(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

