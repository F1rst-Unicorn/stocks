package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;

public class RefreshRepository {

    private Synchroniser synchroniser;

    @Inject
    RefreshRepository(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    public LiveData<StatusCode> synchronise() {
        return synchroniser.synchronise();
    }

    public LiveData<StatusCode> synchroniseFully() {
        return synchroniser.synchroniseFully();
    }
}
