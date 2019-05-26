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
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;

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
        Mockito.when(userManager.get()).thenReturn(Validation.success(Collections.emptyList()));

        ListResponse<User> result = uut.getUsers();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(Collections.emptyList(), result.data);
        Mockito.verify(userManager).get();
    }

    @Test
    public void addingInvalidNameIsRejected() {

        Response result = uut.putUser(null);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validAddingIsSuccessful() {
        String name = "user";
        Mockito.when(userManager.addUser(any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.putUser(name);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(userManager).addUser(new User(name));
    }

    @Test
    public void deletingInvalidVersionIsRejected() {

        Response result = uut.deleteUser(Util.createMockRequest(),
                1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsRejected() {

        Response result = uut.deleteUser(Util.createMockRequest(),
                0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validDeletingIsSuccessful() {
        Mockito.when(userManager.deleteUser(any(), any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteUser(Util.createMockRequest(), 1, 2);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(userManager).deleteUser(new User(1, 2), PrincipalFilterTest.TEST_USER);
    }
}