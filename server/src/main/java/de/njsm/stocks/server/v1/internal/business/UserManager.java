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

package de.njsm.stocks.server.v1.internal.business;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.User;
import de.njsm.stocks.server.v1.internal.data.UserFactory;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserManager {

    private static final Logger LOG = LogManager.getLogger(UserManager.class);

    private DatabaseHandler databaseHandler;

    private AuthAdmin authAdmin;

    public UserManager(DatabaseHandler databaseHandler, AuthAdmin authAdmin) {
        this.databaseHandler = databaseHandler;
        this.authAdmin = authAdmin;
    }

    public void addUser(User userToAdd) {
        if (Principals.isNameValid(userToAdd.name)) {
            databaseHandler.add(userToAdd);
        } else {
            LOG.warn("Tried to addFood invalid user " + userToAdd);
        }
    }

    public Data[] getUsers() {
        return databaseHandler.get(UserFactory.f);
    }

    public void removeUser(User user) {
        List<Integer> deviceIds = databaseHandler.getDeviceIdsOfUser(user);
        deviceIds.forEach(authAdmin::revokeCertificate);
        databaseHandler.remove(user);
    }


}
