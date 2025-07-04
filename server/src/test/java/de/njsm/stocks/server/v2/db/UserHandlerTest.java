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
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class UserHandlerTest extends DbTestCase implements CrudOperationsTest<UserRecord, User> {

    private UserHandler uut;

    @BeforeEach
    public void setup() {
        uut = new UserHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void gettingUsersWorks() {

        var list = getCurrentData();

        assertEquals(getNumberOfEntities(), list.size());
        assertTrue(list.stream().anyMatch(v ->
                v.id() == 1 &&
                v.version() == 0 &&
                v.name().equals("Default")));
        assertTrue(list.stream().anyMatch(v ->
                v.id() == 2 &&
                v.version() == 0 &&
                v.name().equals("Stocks")));
        assertTrue(list.stream().anyMatch(v ->
                v.id() == 3 &&
                v.version() == 0 &&
                v.name().equals("Bob")));
        assertTrue(list.stream().anyMatch(v ->
                v.id() == 4 &&
                v.version() == 0 &&
                v.name().equals("Alice")));
        assertTrue(list.stream().anyMatch(v ->
                v.id() == 5 &&
                v.version() == 0 &&
                v.name().equals("Jack")));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<User>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        BitemporalUser sample = (BitemporalUser) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Override
    public UserForInsertion getInsertable() {
        return UserForInsertion.builder()
                .name("testuser")
                .build();
    }

    @Override
    public CrudDatabaseHandler<UserRecord, User> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 5;
    }

    @Override
    public Versionable<User> getUnknownEntity() {
        return UserForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<User> getWrongVersionEntity() {
        return UserForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<User> getValidEntity() {
        return UserForDeletion.builder()
                .id(5)
                .version(0)
                .build();
    }
}
