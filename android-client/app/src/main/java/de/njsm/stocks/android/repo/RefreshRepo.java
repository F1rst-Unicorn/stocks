package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;

public class RefreshRepo {

    private Synchroniser synchroniser;

    @Inject
    RefreshRepo(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    public LiveData<StatusCode> synchronise() {
        return synchroniser.synchronise();
    }
}
