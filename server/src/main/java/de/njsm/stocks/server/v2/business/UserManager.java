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
import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import de.njsm.stocks.server.v2.db.UserHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import fj.data.Validation;

import java.util.List;

public class UserManager extends BusinessObject<UserRecord, User> implements
        BusinessGettable<UserRecord, User>,
        BusinessAddable<UserRecord, User>,
        BusinessDeletable<UserForDeletion, User> {

    private final UserHandler dbHandler;

    private final UserDeviceHandler deviceHandler;

    private final FoodItemHandler foodItemHandler;

    public UserManager(UserHandler dbHandler,
                       UserDeviceHandler deviceHandler,
                       FoodItemHandler foodItemHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.deviceHandler = deviceHandler;
        this.foodItemHandler = foodItemHandler;
    }

    @Override
    public StatusCode delete(UserForDeletion userToDelete) {
        return runOperation(() -> {
            Validation<StatusCode, List<Identifiable<UserDevice>>> deviceResult = deviceHandler.getDevicesOfUser(userToDelete);

            if (deviceResult.isFail())
                return deviceResult.fail();

            List<Identifiable<UserDevice>> devices = deviceResult.success();

            return foodItemHandler.transferFoodItems(userToDelete, principals.toUser(), devices, principals.toDevice())
                    .bind(() -> dbHandler.delete(userToDelete));
        });
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        deviceHandler.setPrincipals(principals);
        foodItemHandler.setPrincipals(principals);
    }
}
