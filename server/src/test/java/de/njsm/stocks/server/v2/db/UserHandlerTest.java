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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.User;
import de.njsm.stocks.common.api.BitemporalUser;
import de.njsm.stocks.common.api.UserForDeletion;
import de.njsm.stocks.common.api.UserForGetting;
import de.njsm.stocks.common.api.UserForInsertion;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserHandlerTest extends DbTestCase {

    private UserHandler uut;

    @Before
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
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void addingUserWorks() {
        UserForInsertion input = new UserForInsertion("testuser");

        Validation<StatusCode, Integer> result = uut.addReturningId(input);

        Validation<StatusCode, Stream<User>> users = uut.get(false, Instant.EPOCH);
        assertTrue(result.isSuccess());
        List<User> list = users.success().collect(Collectors.toList());
        assertEquals(Integer.valueOf(5), result.success());
        assertTrue(users.isSuccess());
        assertEquals(5, list.size());
        assertThat(list, hasItem(matchesInsertable(input)));
    }

    @Test
    public void deletingUnknownIdIsReported() {

        StatusCode result = uut.delete(new UserForDeletion(99999, 0));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingInvalidVersionIsReported() {

        StatusCode result = uut.delete(new UserForDeletion(1, 999));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void deletingValidDeviceWorks() {
        UserForDeletion input = new UserForDeletion(1, 0);

        StatusCode result = uut.delete(input);

        Validation<StatusCode, Stream<User>> users = uut.get(false, Instant.EPOCH);
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(users.isSuccess());
        List<User> list = users.success().collect(Collectors.toList());
        assertEquals(3, list.size());
        UserForGetting expectedAbsent = new UserForGetting(1, 0, "Bob");
        assertThat(list, not(hasItem(expectedAbsent)));
    }

}
