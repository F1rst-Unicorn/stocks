package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.FoodItem;

import java.io.File;

public class MoveItemTask extends AbstractNetworkTask<Object, Void, Void> {

    private NetworkManager networkManager;

    public MoveItemTask(File exceptionFileDirectory,
                        ServerManager serverManager,
                        NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Void doInBackgroundInternally(Object... params) {
        FoodItem item = (FoodItem) params[0];
        int locationId = (int) params[1];

        serverManager.move(item, locationId);
        return null;
    }

    @Override
    protected void onPostExecute(Void dummy) {
        networkManager.synchroniseData();
    }

}
