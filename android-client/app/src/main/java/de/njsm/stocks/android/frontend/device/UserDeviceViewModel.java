package de.njsm.stocks.android.frontend.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.UserDeviceRepository;
import de.njsm.stocks.android.util.Logger;
import fj.data.Validation;

import javax.inject.Inject;
import java.util.List;

public class UserDeviceViewModel extends ViewModel {

    private static final Logger LOG = new Logger(UserDeviceViewModel.class);

    private LiveData<List<UserDevice>> userDevices;

    private UserDeviceRepository userDeviceRepo;

    @Inject
    public UserDeviceViewModel(UserDeviceRepository userDeviceRepo) {
        this.userDeviceRepo = userDeviceRepo;
    }

    public void init(int userId) {
        if (userDevices == null) {
            LOG.d("Initialising");
            userDevices = userDeviceRepo.getUserDevices(userId);
        }
    }

    LiveData<List<UserDevice>> getDevices() {
        return userDevices;
    }

    LiveData<Validation<StatusCode, ServerTicket>> addUserDevice(String name, int userId) {
        return userDeviceRepo.addUserDevice(name, userId);
    }

    LiveData<StatusCode> deleteUserDevice(UserDevice item) {
        return userDeviceRepo.deleteUserDevice(item);
    }
}
