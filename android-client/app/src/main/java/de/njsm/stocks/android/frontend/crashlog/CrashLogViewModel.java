package de.njsm.stocks.android.frontend.crashlog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.repo.CrashLogRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class CrashLogViewModel extends ViewModel {

    private static final Logger LOG = new Logger(CrashLogViewModel.class);

    private LiveData<List<CrashLog>> data;

    private CrashLogRepository crashLogRepository;

    @Inject
    public CrashLogViewModel(CrashLogRepository crashLogRepository) {
        this.crashLogRepository = crashLogRepository;
    }

    public void init() {
        if (data == null) {
            LOG.d("initialising");
            data = crashLogRepository.getCrashLogs();
        }
    }

    public LiveData<List<CrashLog>> getData() {
        return data;
    }

    public void delete(CrashLog t) {
        crashLogRepository.delete(t);
    }
}
