package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.EanNumber;

import java.io.File;

public class DeleteEanNumberTask extends AbstractNetworkTask<EanNumber, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteEanNumberTask(File exceptionFileDirectory,
                               ServerManager serverManager,
                               NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(EanNumber... params) {
        serverManager.removeEanNumber(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}
