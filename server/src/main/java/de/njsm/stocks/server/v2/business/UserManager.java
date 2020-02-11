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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.UserHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.stream.Stream;

public class UserManager extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(UserManager.class);

    private UserHandler dbHandler;

    private DeviceManager deviceManager;

    private FoodItemHandler foodItemHandler;

    public UserManager(UserHandler dbHandler,
                       DeviceManager deviceManager,
                       FoodItemHandler foodItemHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.deviceManager = deviceManager;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode addUser(User u) {
        return runOperation(() -> dbHandler.add(u)
                .toEither().left().orValue(StatusCode.SUCCESS));
    }

    public Validation<StatusCode, Stream<User>> get(AsyncResponse r) {
        return runFunction(r, () -> {
            dbHandler.setReadOnly();
            return dbHandler.get();
        });
    }

    public StatusCode deleteUser(User userToDelete, Principals currentUser) {
        return runOperation(() -> {
            Validation<StatusCode, List<UserDevice>> devices = deviceManager.getDevicesBelonging(userToDelete);

            if (devices.isFail())
                return devices.fail();

            for (UserDevice device : devices.success()) {
                StatusCode removeCode = deviceManager.removeDeviceInternally(device, currentUser);

                if (removeCode != StatusCode.SUCCESS) {
                    return removeCode;
                }
            }
            StatusCode transferItemsCode = foodItemHandler.transferFoodItems(userToDelete, currentUser.toUser());

            if (transferItemsCode != StatusCode.SUCCESS) {
                return transferItemsCode;
            }

            return dbHandler.delete(userToDelete);
        });
    }

}
