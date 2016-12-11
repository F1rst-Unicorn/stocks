package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DeviceEndpointTest extends BaseTestEndpoint {

    private Config c;
    private String ticket;
    private UserDevice testItem;
    private UserDevice invalidTestItem;

    private DeviceEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(UserDeviceFactory.f))
                .thenReturn(new Data[0]);
        ticket = Ticket.generateTicket();
        testItem = new UserDevice(1, "Mobile", 2);
        invalidTestItem = new UserDevice(1, "Mobile$1", 2);

        uut = new DeviceEndpoint(c);
    }

    @Test
    public void testGettingNumbers() {
        Data[] result = uut.getDevices(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(UserDeviceFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingValidItem() {
        Assert.assertTrue(HttpsUserContextFactory.isNameValid(testItem.name));
        Mockito.when(c.getDbHandler().addDevice(testItem))
                .thenReturn(new Ticket(0, ticket, null));

        Ticket result = uut.addDevice(createMockRequest(), testItem);

        Assert.assertNotNull(result.ticket);
        Assert.assertEquals(Ticket.TICKET_LENGTH, result.ticket.length());
        Mockito.verify(c.getDbHandler()).addDevice(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingInvalidItem() {
        Assert.assertFalse(HttpsUserContextFactory.isNameValid(invalidTestItem.name));
        Mockito.when(c.getDbHandler().addDevice(invalidTestItem))
                .thenReturn(new Ticket(0, null, null));

        Ticket result = uut.addDevice(createMockRequest(), invalidTestItem);

        Assert.assertEquals(0, result.deviceId);
        Assert.assertNull(result.ticket);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingNumber() {
        uut.removeDevice(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).removeDevice(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}
