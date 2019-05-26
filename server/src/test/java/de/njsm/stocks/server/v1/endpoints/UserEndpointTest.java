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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.User;
import de.njsm.stocks.server.v1.internal.business.UserManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class UserEndpointTest extends BaseTestEndpoint {

    private DatabaseHandler handler;

    private UserManager userManager;

    private UserEndpoint uut;

    @Before
    public void setup() {
        userManager = Mockito.mock(UserManager.class);
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new UserEndpoint(userManager, handler);

        Mockito.when(userManager.getUsers())
                .thenReturn(new Data[0]);
    }


    @Test
    public void testAddingDevice() {
        User user = new User(0, "Jack");

        uut.addUser(BaseTestEndpoint.createMockRequest(), user);

        Mockito.verify(userManager).addUser(user);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);
    }

    @Test
    public void idIsClearedByServer() {
        User data = new User(3, "123-123-123");
        User expected = new User(0, "123-123-123");

        uut.addUser(createMockRequest(), data);

        Mockito.verify(userManager).addUser(expected);
    }


    @Test
    public void testGettingDevices() {

        Data[] output = uut.getUsers(BaseTestEndpoint.createMockRequest());

        assertEquals(0, output.length);
        Mockito.verify(userManager).getUsers();
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);

    }

    @Test
    public void testRemovingDevice() {
        User user = new User(0, "Jack");

        uut.removeUser(BaseTestEndpoint.createMockRequest(), user);

        Mockito.verify(userManager).removeUser(user);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);
    }

}
