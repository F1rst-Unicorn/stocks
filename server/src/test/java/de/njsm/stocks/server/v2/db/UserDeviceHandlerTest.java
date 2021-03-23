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
import de.njsm.stocks.server.v2.business.data.*;
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
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }


    @Test
    public void getDevicesWorks() {

        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);

        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(5, list.size());
        assertThat(list, hasItem(new UserDeviceForGetting(1, 0, "Default", 1)));
        assertThat(list, hasItem(new UserDeviceForGetting(2, 0, "mobile", 2)));
        assertThat(list, hasItem(new UserDeviceForGetting(3, 0, "mobile2", 2)));
        assertThat(list, hasItem(new UserDeviceForGetting(4, 0, "laptop", 3)));
        assertThat(list, hasItem(new UserDeviceForGetting(5, 0, "pending_device", 3)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<UserDevice>> result = uut.get(true, Instant.EPOCH);

        BitemporalUserDevice sample = (BitemporalUserDevice) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void addingNewDeviceWorks() {
        UserDeviceForInsertion device = new UserDeviceForInsertion("newDevice", 1);

        Validation<StatusCode, Integer> result = uut.add(device);


        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(6), result.success());
        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(6, list.size());
        assertThat(list, hasItem(new UserDeviceForGetting(6, 0, device.getName(), device.getBelongsTo())));
    }

    @Test
    public void deletingUnknownIdIsReported() {

        StatusCode result = uut.delete(new UserDeviceForDeletion(99999, 0));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingInvalidVersionIsReported() {

        StatusCode result = uut.delete(new UserDeviceForDeletion(1, 9999));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void deletingValidDeviceWorks() {
        UserDeviceForDeletion device = new UserDeviceForDeletion(2, 0);

        StatusCode result = uut.delete(device);

        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(4, list.size());
        assertThat(list, not(hasItem(new UserDeviceForGetting(2, 0, "mobile", 2))));
    }

    @Test
    public void gettingDevicesOfUserWorks() {

        Validation<StatusCode, List<Identifiable<UserDevice>>> result = uut.getDevicesOfUser(new UserForDeletion(2, 2));

        assertTrue(result.isSuccess());
        assertEquals(2, result.success().size());
        assertThat(result.success(), hasItem(new UserDeviceForPrincipals(2)));
        assertThat(result.success(), hasItem(new UserDeviceForPrincipals(3)));
    }
}
