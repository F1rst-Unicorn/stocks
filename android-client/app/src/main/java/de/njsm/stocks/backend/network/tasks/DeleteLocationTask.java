package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.Location;

import java.io.File;

public class DeleteLocationTask extends AbstractNetworkTask<Location, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteLocationTask(File exceptionFileDirectory,
                              ServerManager serverManager,
                              NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(Location... params) {
        serverManager.removeLocation(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

