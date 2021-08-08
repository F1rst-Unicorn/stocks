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
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
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

public class UserHandlerTest extends DbTestCase implements CrudOperationsTest<UserRecord, User> {

    private UserHandler uut;

    @BeforeEach
    public void setup() {
        uut = new UserHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void gettingUsersWorks() {

        Validation<StatusCode, Stream<User>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<User> list = result.success().collect(Collectors.toList());
        assertEquals(4, list.size());
        assertThat(list, hasItem(new UserForGetting(1, 0, "Default")));
        assertThat(list, hasItem(new UserForGetting(2, 0, "Bob")));
        assertThat(list, hasItem(new UserForGetting(3, 0, "Alice")));
        assertThat(list, hasItem(new UserForGetting(4, 0, "Jack")));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<User>> result = uut.get(true, Instant.EPOCH);

        BitemporalUser sample = (BitemporalUser) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Override
    public UserForInsertion getInsertable() {
        return new UserForInsertion("testuser");
    }

    @Override
    public CrudDatabaseHandler<UserRecord, User> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 4;
    }

    @Override
    public Versionable<User> getUnknownEntity() {
        return new UserForDeletion(getNumberOfEntities() + 1, 0);
    }

    @Override
    public Versionable<User> getWrongVersionEntity() {
        return new UserForDeletion(getValidEntity().id(), getValidEntity().version() + 1);
    }

    @Override
    public Versionable<User> getValidEntity() {
        return new UserForDeletion(1, 0);
    }
}
