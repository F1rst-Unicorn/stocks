package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.UserDevice;

import java.io.File;

public class DeleteDeviceTask extends AbstractNetworkTask<UserDevice, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteDeviceTask(File exceptionFileDirectory,
                            ServerManager serverManager,
                            NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(UserDevice... params) {
        serverManager.removeDevice(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}

