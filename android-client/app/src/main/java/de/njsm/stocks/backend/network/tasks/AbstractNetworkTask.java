package de.njsm.stocks.backend.network.tasks;

import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.util.AbstractAsyncTask;

import java.io.File;

public abstract class AbstractNetworkTask<S, T, U> extends AbstractAsyncTask<S, T, U> {

    protected ServerManager serverManager;

    public AbstractNetworkTask(File exceptionFileDirectory, ServerManager serverManager) {
        super(exceptionFileDirectory);
        this.serverManager = serverManager;
    }
}
