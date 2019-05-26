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

package de.njsm.stocks.server.v1.internal.business;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.User;
import de.njsm.stocks.server.v1.internal.data.UserFactory;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserManagerTest {

    private DatabaseHandler databaseHandler;

    private AuthAdmin authAdmin;

    private UserManager uut;

    @Before
    public void setup() {
        databaseHandler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(AuthAdmin.class);
        uut = new UserManager(databaseHandler, authAdmin);
    }

    @Test
    public void testGettingUsers() {
        Mockito.when(databaseHandler.get(UserFactory.f)).thenReturn(new Data[0]);

        Data[] actual = uut.getUsers();

        assertEquals(0, actual.length);
        Mockito.verify(databaseHandler).get(UserFactory.f);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void testAddingUser() {
        User user = new User(3, "Alice");

        uut.addUser(user);

        Mockito.verify(databaseHandler).add(user);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void invalidNamesAreNotAdded() {
        User user = new User(3, "Alice$2");

        uut.addUser(user);

        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }

    @Test
    public void testRemovingUser() {
        User user = new User(3, "Alice");
        List<Integer> ids = new ArrayList<>();
        ids.add(3);
        ids.add(4);
        Mockito.when(databaseHandler.getDeviceIdsOfUser(user)).thenReturn(ids);

        uut.removeUser(user);

        Mockito.verify(databaseHandler).getDeviceIdsOfUser(user);
        Mockito.verify(databaseHandler).remove(user);
        Mockito.verify(authAdmin).revokeCertificate(3);
        Mockito.verify(authAdmin).revokeCertificate(4);
        Mockito.verifyNoMoreInteractions(databaseHandler);
        Mockito.verifyNoMoreInteractions(authAdmin);
    }
}