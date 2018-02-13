package de.njsm.stocks.server.internal.business;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.UserDeviceFactory;
import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class DevicesManagerTest {

    private DatabaseHandler databaseHandler;

    private AuthAdmin authAdmin;

    private DevicesManager uut;

    @Before
    public void setup() {
        databaseHandler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(AuthAdmin.class);
        uut = new DevicesManager(databaseHandler, authAdmin);
    }

    @Test
    public void testGettingDevices() {
        Mockito.when(databaseHandler.get(UserDeviceFactory.f)).thenReturn(new Data[0]);

        Data[] actual = uut.getDevices();

        assertEquals(0, actual.length);
        Mockito.verify(databaseHandler).get(UserDeviceFactory.f);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void testAddingDevice() {
        UserDevice userDevice = new UserDevice(3, "Mobile", 1);
        Mockito.when(databaseHandler.add(userDevice)).thenReturn(userDevice.id);

        Ticket actual = uut.addDevice(userDevice);

        assertFalse(actual.ticket.isEmpty());
        assertEquals(userDevice.id, actual.deviceId);
        Mockito.verify(databaseHandler).add(userDevice);
        Mockito.verify(databaseHandler).add(actual);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void invalidNamesAreNotAdded() {
        UserDevice userDevice = new UserDevice(3, "Mobile$1", 1);

        Ticket actual = uut.addDevice(userDevice);

        assertNull(actual.ticket);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void testRemovingUser() {
        UserDevice userDevice = new UserDevice(3, "Mobile", 1);

        uut.removeDevice(userDevice);

        Mockito.verify(databaseHandler).remove(userDevice);
        Mockito.verify(authAdmin).revokeCertificate(userDevice.id);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }
}