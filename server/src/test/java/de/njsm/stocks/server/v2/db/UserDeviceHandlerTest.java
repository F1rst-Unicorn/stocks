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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.data.UserDeviceForPrincipals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserDeviceHandlerTest extends DbTestCase implements CrudOperationsTest<UserDeviceRecord, UserDevice> {

    private UserDeviceHandler uut;

    @BeforeEach
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
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Override
    public UserDeviceForInsertion getInsertable() {
        return new UserDeviceForInsertion("newDevice", 1);
    }

    @Test
    public void gettingDevicesOfUserWorks() {

        Validation<StatusCode, List<Identifiable<UserDevice>>> result = uut.getDevicesOfUser(new UserForDeletion(2, 2));

        assertTrue(result.isSuccess());
        assertEquals(2, result.success().size());
        assertThat(result.success(), hasItem(UserDeviceForPrincipals.builder().id(2).build()));
        assertThat(result.success(), hasItem(UserDeviceForPrincipals.builder().id(3).build()));
    }

    @Override
    public CrudDatabaseHandler<UserDeviceRecord, UserDevice> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 5;
    }

    @Override
    public Versionable<UserDevice> getUnknownEntity() {
        return new UserDeviceForDeletion(getNumberOfEntities() + 1, 0);
    }

    @Override
    public Versionable<UserDevice> getWrongVersionEntity() {
        return new UserDeviceForDeletion(getValidEntity().id(), getValidEntity().version() + 1);
    }

    @Override
    public Versionable<UserDevice> getValidEntity() {
        return new UserDeviceForDeletion(2, 0);
    }
}
