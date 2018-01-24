package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.business.DevicesManager;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class DeviceEndpointTest extends BaseTestEndpoint {

    private Ticket ticket;

    private DeviceEndpoint uut;

    private DatabaseHandler handler;

    private DevicesManager devicesManager;

    @Before
    public void setup() {
        devicesManager = Mockito.mock(DevicesManager.class);
        handler = Mockito.mock(DatabaseHandler.class);
        UserContextFactory contextFactory = Mockito.mock(UserContextFactory.class);
        uut = new DeviceEndpoint(devicesManager, handler, contextFactory);

        Mockito.when(devicesManager.getDevices())
                .thenReturn(new Data[0]);
        Mockito.when(contextFactory.getPrincipals(any()))
                .thenReturn(TEST_USER);
        ticket = new Ticket(3, Ticket.generateTicket(), "");
    }

    @Test
    public void testAddingDevice() {
        UserDevice userDevice = new UserDevice(0, "Mobile", 3);
        Mockito.when(devicesManager.addDevice(userDevice)).thenReturn(ticket);

        Ticket actual = uut.addDevice(BaseTestEndpoint.createMockRequest(), userDevice);

        assertEquals(ticket, actual);
        Mockito.verify(devicesManager).addDevice(userDevice);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);
    }

    @Test
    public void testGettingDevices() {

        Data[] output = uut.getDevices(BaseTestEndpoint.createMockRequest());

        assertEquals(0, output.length);
        Mockito.verify(devicesManager).getDevices();
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);

    }

    @Test
    public void testRemovingDevice() {
        UserDevice userDevice = new UserDevice(0, "Mobile", 3);

        uut.removeDevice(BaseTestEndpoint.createMockRequest(), userDevice);

        Mockito.verify(devicesManager).removeDevice(userDevice);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(devicesManager);
    }
}
