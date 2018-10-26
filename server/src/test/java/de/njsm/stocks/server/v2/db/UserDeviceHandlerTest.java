package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class UserDeviceHandlerTest extends DbTestCase {

    private UserDeviceHandler uut;

    @Before
    public void setup() {
        uut = new UserDeviceHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                new InsertVisitor<>());
    }


    @Test
    public void getDevicesWorks() {

        Validation<StatusCode, List<UserDevice>> devices = uut.get();

        assertTrue(devices.isSuccess());
        assertEquals(4, devices.success().size());
        assertThat(devices.success(), hasItem(new UserDevice(1, 0, "mobile", 1)));
        assertThat(devices.success(), hasItem(new UserDevice(2, 0, "mobile2", 1)));
        assertThat(devices.success(), hasItem(new UserDevice(3, 0, "laptop", 2)));
        assertThat(devices.success(), hasItem(new UserDevice(4, 0, "pending_device", 2)));
    }

    @Test
    public void addingNewDeviceWorks() {
        UserDevice device = new UserDevice(1, 0, "newDevice", 1);

        Validation<StatusCode, Integer> result = uut.add(device);


        Validation<StatusCode, List<UserDevice>> devices = uut.get();
        assertTrue(result.isSuccess());
        assertEquals(new Integer(5), result.success());
        assertTrue(devices.isSuccess());
        assertEquals(5, devices.success().size());
        device.id = 5;
        assertThat(devices.success(), hasItem(device));
    }

    @Test
    public void deletingUnknownIdIsReported() {

        StatusCode result = uut.delete(new UserDevice(99999, 0, "", 1));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingInvalidVersionIsReported() {

        StatusCode result = uut.delete(new UserDevice(1, 999, "", 1));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void deletingValidDeviceWorks() {
        UserDevice device = new UserDevice(1, 0, "newDevice", 1);

        StatusCode result = uut.delete(device);

        Validation<StatusCode, List<UserDevice>> devices = uut.get();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(devices.isSuccess());
        assertEquals(3, devices.success().size());
        assertThat(devices.success(), not(hasItem(device)));
    }

    @Test
    public void gettingDevicesOfUserWorks() {

        Validation<StatusCode, List<UserDevice>> result = uut.getDevicesOfUser(new User(1, 2));

        assertTrue(result.isSuccess());
        assertEquals(2, result.success().size());
        assertThat(result.success(), hasItem(new UserDevice(1, 0, "mobile", 1)));
        assertThat(result.success(), hasItem(new UserDevice(2, 0, "mobile2", 1)));
    }
}