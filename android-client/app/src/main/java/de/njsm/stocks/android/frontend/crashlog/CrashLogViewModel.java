package de.njsm.stocks.android.frontend.crashlog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.business.CrashLog;
import de.njsm.stocks.android.repo.CrashLogRepo;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class CrashLogViewModel extends ViewModel {

    private static final Logger LOG = new Logger(CrashLogViewModel.class);

    private LiveData<List<CrashLog>> data;

    private CrashLogRepo crashLogRepo;

    @Inject
    public CrashLogViewModel(CrashLogRepo crashLogRepo) {
        this.crashLogRepo = crashLogRepo;
    }

    public void init() {
        if (data == null) {
            LOG.d("initialising");
            data = crashLogRepo.getCrashLogs();
        }
    }

    public LiveData<List<CrashLog>> getData() {
        return data;
    }

    public void delete(CrashLog t) {
        crashLogRepo.delete(t);
    }
}
