package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.UserDeviceFactory;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

public class DeviceEndpointTest extends BaseTestEndpoint {

    private String ticket;
    private UserDevice testItem;
    private UserDevice invalidTestItem;

    private DeviceEndpoint uut;

    private DatabaseHandler handler;

    private UserContextFactory authAdmin;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        uut = new DeviceEndpoint(handler, authAdmin);

        Mockito.when(handler.get(UserDeviceFactory.f))
                .thenReturn(new Data[0]);
        Mockito.when(authAdmin.getPrincipals(any()))
                .thenReturn(TEST_USER);
        ticket = Ticket.generateTicket();
        testItem = new UserDevice(1, "Mobile", 2);
        invalidTestItem = new UserDevice(1, "Mobile$1", 2);
    }

    @Test
    public void testGettingDevice() {
        Data[] result = uut.getDevices(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(UserDeviceFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingValidItem() {
        Assert.assertTrue(HttpsUserContextFactory.isNameValid(testItem.name));
        Mockito.when(handler.addDevice(testItem))
                .thenReturn(new Ticket(0, ticket, null));

        Ticket result = uut.addDevice(createMockRequest(), testItem);

        Assert.assertNotNull(result.ticket);
        Assert.assertEquals(Ticket.TICKET_LENGTH, result.ticket.length());
        Mockito.verify(handler).addDevice(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingInvalidItem() {
        Assert.assertFalse(HttpsUserContextFactory.isNameValid(invalidTestItem.name));
        Mockito.when(handler.addDevice(invalidTestItem))
                .thenReturn(new Ticket(0, null, null));

        Ticket result = uut.addDevice(createMockRequest(), invalidTestItem);

        Assert.assertEquals(0, result.deviceId);
        Assert.assertNull(result.ticket);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testRemovingDevice() {
        uut.removeDevice(createMockRequest(), testItem);

        Mockito.verify(handler).removeDevice(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void returnEmptyTicketIfNotGotFromDatabase() {
        Mockito.when(handler.addDevice(any())).thenReturn(null);

        Ticket result = uut.addDevice(createMockRequest(), testItem);

        Mockito.verify(handler).addDevice(any());
        Mockito.verifyNoMoreInteractions(handler);
        Assert.assertNull(result.pemFile);
        Assert.assertNull(result.ticket);
    }
}
