package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Food;

import java.io.File;

public class NewFoodTask extends AbstractAsyncTask<Food, Void, Integer> {

    private NetworkManager networkManager;

    public NewFoodTask(File exceptionFileDirectory, NetworkManager networkManager) {
        super(exceptionFileDirectory);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(Food... params) {
        ServerManager.m.addFood(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

