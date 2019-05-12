package de.njsm.stocks.android.frontend.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.RefreshRepo;

import javax.inject.Inject;

public class RefreshViewModel extends ViewModel {

    private RefreshRepo refreshRepo;

    @Inject
    public RefreshViewModel(RefreshRepo refreshRepo) {
        this.refreshRepo = refreshRepo;
    }

    public LiveData<StatusCode> refresh() {
        return refreshRepo.synchronise();
    }
}
