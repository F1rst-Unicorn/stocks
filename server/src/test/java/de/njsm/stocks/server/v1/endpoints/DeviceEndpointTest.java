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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.Ticket;
import de.njsm.stocks.server.v1.internal.data.UserDevice;
import de.njsm.stocks.server.v1.internal.business.DevicesManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class DeviceEndpointTest extends de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint {

    private Ticket ticket;

    private de.njsm.stocks.server.v1.endpoints.DeviceEndpoint uut;

    private DatabaseHandler handler;

    private DevicesManager devicesManager;

    @Before
    public void setup() {
        devicesManager = Mockito.mock(DevicesManager.class);
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new de.njsm.stocks.server.v1.endpoints.DeviceEndpoint(devicesManager, handler);

        Mockito.when(devicesManager.getDevices())
                .thenReturn(new Data[0]);
        ticket = new Ticket(3, Ticket.generateTicket(), "");
    }

    @Test
    public void testAddingDevice() {
        UserDevice userDevice = new UserDevice(0, "Mobile", 3);
        Mockito.when(devicesManager.addDevice(userDevice)).thenReturn(ticket);

        Ticket actual = uut.addDevice(createMockRequest(), userDevice);

        assertEquals(ticket, actual);
        Mockito.verify(devicesManager).addDevice(userDevice);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);
    }

    @Test
    public void idIsClearedByServer() {
        UserDevice userDevice = new UserDevice(3, "Mobile", 3);
        UserDevice expected = new UserDevice(0, "Mobile", 3);
        Mockito.when(devicesManager.addDevice(userDevice)).thenReturn(ticket);

        uut.addDevice(createMockRequest(), userDevice);

        Mockito.verify(devicesManager).addDevice(expected);
    }

    @Test
    public void testGettingDevices() {

        Data[] output = uut.getDevices(de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint.createMockRequest());

        assertEquals(0, output.length);
        Mockito.verify(devicesManager).getDevices();
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);

    }

    @Test
    public void testRemovingDevice() {
        UserDevice userDevice = new UserDevice(0, "Mobile", 3);

        uut.removeDevice(de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint.createMockRequest(), userDevice);

        Mockito.verify(devicesManager).removeDevice(userDevice);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);
    }
}
