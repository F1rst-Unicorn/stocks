package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.Food;

import java.io.File;

public class NewFoodTask extends AbstractNetworkTask<Food, Void, Integer> {

    private NetworkManager networkManager;

    public NewFoodTask(File exceptionFileDirectory,
                       ServerManager serverManager,
                       NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(Food... params) {
        serverManager.addFood(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

