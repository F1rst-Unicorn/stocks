package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.FoodItem;

import java.io.File;

public class DeleteItemTask extends AbstractNetworkTask<FoodItem, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteItemTask(File exceptionFileDirectory,
                          ServerManager serverManager,
                          NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(FoodItem... params) {
        serverManager.removeItem(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}
