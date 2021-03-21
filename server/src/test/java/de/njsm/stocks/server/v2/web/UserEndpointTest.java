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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserForDeletion;
import de.njsm.stocks.server.v2.business.data.UserForInsertion;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class UserEndpointTest {

    private UserEndpoint uut;

    private UserManager userManager;

    @Before
    public void setup() {
        userManager = Mockito.mock(UserManager.class);

        uut = new UserEndpoint(userManager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(userManager);
    }

    @Test
    public void getUsers() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(userManager.get(r, false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));

        uut.get(r, 0, null);

        ArgumentCaptor<StreamResponse<User>> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.SUCCESS, c.getValue().getStatus());
        assertEquals(0, c.getValue().data.count());
        Mockito.verify(userManager).get(r, false, Instant.EPOCH);
    }

    @Test
    public void getUsersFromInvalidStartingPoint() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);

        uut.get(r, 1, "invalid");

        ArgumentCaptor<Response> c = ArgumentCaptor.forClass(StreamResponse.class);
        verify(r).resume(c.capture());
        assertEquals(StatusCode.INVALID_ARGUMENT, c.getValue().getStatus());
    }

    @Test
    public void addingInvalidNameIsRejected() {
        Response result = uut.putUser(createMockRequest(), null);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addUserContainingDollarIsRejected() {
        String name = "John$1";
        Response result = uut.putUser(createMockRequest(), name);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void addUserContainingEqualSignIsRejected() {
        String name = "John=1";
        Response result = uut.putUser(createMockRequest(), name);
        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validAddingIsSuccessful() {
        String name = "user";
        Mockito.when(userManager.addUser(any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.putUser(createMockRequest(), name);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(userManager).addUser(new UserForInsertion(name));
        Mockito.verify(userManager).setPrincipals(TEST_USER);
    }

    @Test
    public void deletingInvalidVersionIsRejected() {

        Response result = uut.delete(Util.createMockRequest(),
                1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsRejected() {

        Response result = uut.delete(Util.createMockRequest(),
                0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validDeletingIsSuccessful() {
        Mockito.when(userManager.delete(any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.delete(Util.createMockRequest(), 1, 2);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(userManager).delete(new UserForDeletion(1, 2));
        Mockito.verify(userManager).setPrincipals(TEST_USER);
    }
}
