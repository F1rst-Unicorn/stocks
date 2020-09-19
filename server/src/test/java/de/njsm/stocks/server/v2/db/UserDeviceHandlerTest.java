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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDeviceHandlerTest extends DbTestCase {

    private UserDeviceHandler uut;

    @Before
    public void setup() {
        uut = new UserDeviceHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT,
                new InsertVisitor<>());
        uut.setPrincipals(TEST_USER);
    }


    @Test
    public void getDevicesWorks() {

        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);

        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(4, list.size());
        assertThat(list, hasItem(new UserDevice(1, 0, "mobile", 1)));
        assertThat(list, hasItem(new UserDevice(2, 0, "mobile2", 1)));
        assertThat(list, hasItem(new UserDevice(3, 0, "laptop", 2)));
        assertThat(list, hasItem(new UserDevice(4, 0, "pending_device", 2)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<UserDevice>> result = uut.get(true, Instant.EPOCH);

        UserDevice sample = result.success().findAny().get();
        assertNotNull(sample.validTimeStart);
        assertNotNull(sample.validTimeEnd);
        assertNotNull(sample.transactionTimeStart);
        assertNotNull(sample.transactionTimeEnd);
    }

    @Test
    public void addingNewDeviceWorks() {
        UserDevice device = new UserDevice(1, 0, "newDevice", 1);

        Validation<StatusCode, Integer> result = uut.add(device);


        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);
        assertTrue(result.isSuccess());
        assertEquals(new Integer(5), result.success());
        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(5, list.size());
        device.id = 5;
        assertThat(list, hasItem(device));
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

        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        assertThat(list, not(hasItem(device)));
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
