package de.njsm.stocks.android.frontend.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.RefreshRepository;

import javax.inject.Inject;

public class RefreshViewModel extends ViewModel {

    private RefreshRepository refreshRepository;

    @Inject
    public RefreshViewModel(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    public LiveData<StatusCode> refresh() {
        return refreshRepository.synchronise();
    }

    public LiveData<StatusCode> refreshComplete() {
        return refreshRepository.synchroniseFully();
    }
}
