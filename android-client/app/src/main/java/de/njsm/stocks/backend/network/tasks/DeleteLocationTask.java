package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Location;

import java.io.File;

public class DeleteLocationTask extends AbstractAsyncTask<Location, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteLocationTask(File exceptionFileDirectory, NetworkManager networkManager) {
        super(exceptionFileDirectory);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(Location... params) {
        ServerManager.m.removeLocation(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

