/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.frontend.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.common.api.StatusCode;
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
