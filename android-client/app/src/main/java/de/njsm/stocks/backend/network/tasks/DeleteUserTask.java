package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.common.data.User;

import java.io.File;

public class DeleteUserTask extends AbstractNetworkTask<User, Void, Integer> {

    private NetworkManager networkManager;

    public DeleteUserTask(File exceptionFileDirectory,
                          ServerManager serverManager,
                          NetworkManager networkManager) {
        super(exceptionFileDirectory, serverManager);
        this.networkManager = networkManager;
    }

    @Override
    protected Integer doInBackgroundInternally(User... params) {
        serverManager.removeUser(params[0]);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        networkManager.synchroniseData();
    }
}
