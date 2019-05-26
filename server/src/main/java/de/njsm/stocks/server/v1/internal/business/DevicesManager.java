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
import de.njsm.stocks.server.v1.internal.data.Ticket;
import de.njsm.stocks.server.v1.internal.data.UserDevice;
import de.njsm.stocks.server.v1.internal.data.UserDeviceFactory;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevicesManager {

    private static final Logger LOG = LogManager.getLogger(DevicesManager.class);

    private DatabaseHandler databaseHandler;

    private AuthAdmin authAdmin;

    public DevicesManager(DatabaseHandler databaseHandler, AuthAdmin authAdmin) {
        this.databaseHandler = databaseHandler;
        this.authAdmin = authAdmin;
    }

    public Ticket addDevice(UserDevice d) {
        if (Principals.isNameValid(d.name)) {
            return setupNewDevice(d);
        } else {
            LOG.warn("Tried to add invalid device " + d.name);
            return new Ticket();
        }
    }

    public void removeDevice(UserDevice d) {
        databaseHandler.remove(d);
        authAdmin.revokeCertificate(d.id);
    }

    public Data[] getDevices() {
        return databaseHandler.get(UserDeviceFactory.f);
    }

    private Ticket setupNewDevice(UserDevice d) {
        int deviceId = databaseHandler.add(d);

        if (deviceId != 0) {
            Ticket newTicket = generateNewTicket(deviceId);
            databaseHandler.add(newTicket);
            return newTicket;
        } else {
            return new Ticket();
        }
    }

    private Ticket generateNewTicket(int deviceId) {
        return new Ticket(
                deviceId,
                Ticket.generateTicket(),
                null);
    }
}
