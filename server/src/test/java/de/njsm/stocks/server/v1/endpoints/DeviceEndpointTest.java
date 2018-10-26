package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
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
