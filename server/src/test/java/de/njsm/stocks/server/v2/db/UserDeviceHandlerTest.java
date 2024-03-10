/*
 * stocks is client-server program to manage a household's food stock
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
 *
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
        uut = new UserDeviceHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }


    @Test
    public void getDevicesWorks() {

        Validation<StatusCode, Stream<UserDevice>> devices = uut.get(false, Instant.EPOCH);

        assertTrue(devices.isSuccess());
        List<UserDevice> list = devices.success().collect(Collectors.toList());
        assertEquals(6, list.size());
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(1)
                .version(0)
                .name("Default")
                .belongsTo(1)
                .build()));
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(2)
                .version(0)
                .name("Job Runner")
                .belongsTo(2)
                .build()));
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(3)
                .version(0)
                .name("mobile")
                .belongsTo(3)
                .build()));
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(4)
                .version(0)
                .name("mobile2")
                .belongsTo(3)
                .build()));
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(5)
                .version(0)
                .name("laptop")
                .belongsTo(4)
                .build()));
        assertThat(list, hasItem(UserDeviceForGetting.builder()
                .id(6)
                .version(0)
                .name("pending_device")
                .belongsTo(4)
                .build()));
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
        return UserDeviceForInsertion.builder()
                .name("newDevice")
                .belongsTo(1)
                .build();
    }

    @Test
    public void gettingDevicesOfUserWorks() {

        Validation<StatusCode, List<Identifiable<UserDevice>>> result = uut.getDevicesOfUser(UserForDeletion.builder()
                .id(3)
                .version(2)
                .build());

        assertTrue(result.isSuccess());
        assertEquals(2, result.success().size());
        assertThat(result.success(), hasItem(UserDeviceForPrincipals.builder().id(3).build()));
        assertThat(result.success(), hasItem(UserDeviceForPrincipals.builder().id(4).build()));
    }

    @Test
    void gettingDevicesOfUserOmitsTechnicalUsers() {
        Validation<StatusCode, List<Identifiable<UserDevice>>> result = uut.getDevicesOfUser(UserForDeletion.builder()
                .id(2)
                .version(0)
                .build());

        assertTrue(result.isSuccess());
        assertEquals(0, result.success().size());
    }

    @Test
    void technicalUserIsReportedCorrectly() {
        assertTrue(uut.isTechnicalUser(UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build()).success());
        assertFalse(uut.isTechnicalUser(UserDeviceForDeletion.builder()
                .id(1)
                .version(0)
                .build()).success());
    }

    @Override
    public CrudDatabaseHandler<UserDeviceRecord, UserDevice> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 6;
    }

    @Override
    public Versionable<UserDevice> getUnknownEntity() {
        return UserDeviceForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<UserDevice> getWrongVersionEntity() {
        return UserDeviceForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<UserDevice> getValidEntity() {
        return UserDeviceForDeletion.builder()
                .id(2)
                .version(0)
                .build();
    }
}
