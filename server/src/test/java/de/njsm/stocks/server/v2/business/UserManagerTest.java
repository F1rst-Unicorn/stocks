package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.UserHandler;
import de.njsm.stocks.server.v2.web.PrincipalFilterTest;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;

public class UserManagerTest {

    private UserManager uut;

    private UserHandler userDbHandler;

    private DeviceManager deviceManager;

    @Before
    public void setup() {
        userDbHandler = Mockito.mock(UserHandler.class);
        deviceManager = Mockito.mock(DeviceManager.class);

        uut = new UserManager(userDbHandler, deviceManager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(userDbHandler);
        Mockito.verifyNoMoreInteractions(deviceManager);
    }

    @Test
    public void getUsersWorks() {
        Mockito.when(userDbHandler.get()).thenReturn(Validation.success(Collections.emptyList()));

        Validation<StatusCode, List<User>> result = uut.get();

        assertTrue(result.isSuccess());
        Mockito.verify(userDbHandler).get();
    }

    @Test
    public void successfulAddingWorks() {
        User input = new User("fdsa");
        Mockito.when(userDbHandler.add(any())).thenReturn(Validation.success(4));

        StatusCode result = uut.addUser(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(userDbHandler).add(input);
    }

    @Test
    public void failingAddingWorks() {
        User input = new User("fdsa");
        Mockito.when(userDbHandler.add(any())).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        StatusCode result = uut.addUser(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(userDbHandler).add(input);
    }

    @Test
    public void deleteWithFailingDeviceRetrieval() {
        StatusCode code = StatusCode.DATABASE_UNREACHABLE;
        User input = new User(1, 2, "user");
        Mockito.when(deviceManager.getDevicesBelonging(input)).thenReturn(Validation.fail(code));

        StatusCode result = uut.deleteUser(input, PrincipalFilterTest.TEST_USER);

        assertEquals(code, result);
        Mockito.verify(deviceManager).getDevicesBelonging(input);
    }

    @Test
    public void deleteWithFailingDeviceRemoval() {
        StatusCode code = StatusCode.DATABASE_UNREACHABLE;
        List<UserDevice> devices = new LinkedList<>();
        User input = new User(1, 2, "user");
        devices.add(new UserDevice(1, 2, "fdsa", input.id));
        devices.add(new UserDevice(2, 2, "fdsa", input.id));
        devices.add(new UserDevice(3, 2, "fdsa", input.id));
        Mockito.when(deviceManager.getDevicesBelonging(input)).thenReturn(Validation.success(devices));
        Mockito.when(deviceManager.removeDevice(devices.get(0), PrincipalFilterTest.TEST_USER))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(deviceManager.removeDevice(devices.get(1), PrincipalFilterTest.TEST_USER))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(deviceManager.removeDevice(devices.get(2), PrincipalFilterTest.TEST_USER))
                .thenReturn(code);

        StatusCode result = uut.deleteUser(input, PrincipalFilterTest.TEST_USER);

        assertEquals(code, result);
        Mockito.verify(deviceManager).getDevicesBelonging(input);
        Mockito.verify(deviceManager).removeDevice(devices.get(0), PrincipalFilterTest.TEST_USER);
        Mockito.verify(deviceManager).removeDevice(devices.get(1), PrincipalFilterTest.TEST_USER);
        Mockito.verify(deviceManager).removeDevice(devices.get(2), PrincipalFilterTest.TEST_USER);
    }

    @Test
    public void deleteSuccessfully() {
        List<UserDevice> devices = new LinkedList<>();
        User input = new User(1, 2, "user");
        devices.add(new UserDevice(1, 2, "fdsa", input.id));
        devices.add(new UserDevice(2, 2, "fdsa", input.id));
        devices.add(new UserDevice(3, 2, "fdsa", input.id));
        Mockito.when(deviceManager.getDevicesBelonging(input)).thenReturn(Validation.success(devices));
        Mockito.when(deviceManager.removeDevice(devices.get(0), PrincipalFilterTest.TEST_USER))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(deviceManager.removeDevice(devices.get(1), PrincipalFilterTest.TEST_USER))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(deviceManager.removeDevice(devices.get(2), PrincipalFilterTest.TEST_USER))
                .thenReturn(StatusCode.SUCCESS);
        Mockito.when(userDbHandler.delete(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.deleteUser(input, PrincipalFilterTest.TEST_USER);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(deviceManager).getDevicesBelonging(input);
        Mockito.verify(deviceManager).removeDevice(devices.get(0), PrincipalFilterTest.TEST_USER);
        Mockito.verify(deviceManager).removeDevice(devices.get(1), PrincipalFilterTest.TEST_USER);
        Mockito.verify(deviceManager).removeDevice(devices.get(2), PrincipalFilterTest.TEST_USER);
        Mockito.verify(userDbHandler).delete(input);
    }
}