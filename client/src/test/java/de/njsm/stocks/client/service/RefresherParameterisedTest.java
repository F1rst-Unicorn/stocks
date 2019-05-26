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

package de.njsm.stocks.client.service;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class RefresherParameterisedTest {

    private Refresher uut;

    private ServerManager serverManager;

    private DatabaseManager dbManager;

    @Parameterized.Parameter
    public String tableName;

    @Before
    public void setup() throws Exception {
        serverManager = mock(ServerManager.class);
        dbManager = mock(DatabaseManager.class);
        uut = new Refresher(serverManager, dbManager);
    }

    @Parameterized.Parameters(name = "Table {0}")
    public static Iterable<Object[]> getTables() {
        return Arrays.asList(
            new Object[][] {
                {"User"},
                {"User_device"},
                {"Location"},
                {"Food"},
                {"Food_item"}
        });
    }

    @Test
    public void refreshingTableWorks() throws Exception {
        Update localUpdate = new Update(tableName, Instant.ofEpochMilli(0L));
        Update serverUpdate = new Update(tableName, Instant.ofEpochMilli(1L));
        List<Update> list = Collections.singletonList(localUpdate);
        Update[] array = new Update[] {serverUpdate};
        when(serverManager.getUpdates()).thenReturn(Arrays.asList(array));
        when(dbManager.getUpdates()).thenReturn(list);
        when(serverManager.getDevices()).thenReturn(Collections.emptyList());
        when(serverManager.getUsers()).thenReturn(Collections.emptyList());
        when(serverManager.getLocations()).thenReturn(Collections.emptyList());
        when(serverManager.getFood()).thenReturn(Collections.emptyList());
        when(serverManager.getFoodItems()).thenReturn(Collections.emptyList());

        uut.refresh();

        verify(dbManager).getUpdates();
        verify(serverManager).getUpdates();
        verify(dbManager).writeUpdates(Arrays.asList(array));
        switch (tableName) {
            case "User":
                verify(serverManager).getUsers();
                verify(dbManager).writeUsers(Collections.emptyList());
                break;
            case "Location":
                verify(serverManager).getLocations();
                verify(dbManager).writeLocations(Collections.emptyList());
                break;
            case "Food":
                verify(serverManager).getFood();
                verify(dbManager).writeFood(Collections.emptyList());
                break;
            case "User_device":
                verify(serverManager).getDevices();
                verify(dbManager).writeDevices(Collections.emptyList());
                break;
            case "Food_item":
                verify(serverManager).getFoodItems();
                verify(dbManager).writeFoodItems(Collections.emptyList());
                break;
        }
    }

    @Test
    public void noRefreshOnSameDate() throws Exception {
        Update localUpdate = new Update(tableName, Instant.ofEpochMilli(1L));
        Update serverUpdate = new Update(tableName, Instant.ofEpochMilli(1L));
        List<Update> list = Collections.singletonList(localUpdate);
        Update[] array = new Update[] {serverUpdate};
        when(serverManager.getUpdates()).thenReturn(Arrays.asList(array));
        when(dbManager.getUpdates()).thenReturn(list);
        when(serverManager.getDevices()).thenReturn(Collections.emptyList());
        when(serverManager.getUsers()).thenReturn(Collections.emptyList());
        when(serverManager.getLocations()).thenReturn(Collections.emptyList());
        when(serverManager.getFood()).thenReturn(Collections.emptyList());
        when(serverManager.getFoodItems()).thenReturn(Collections.emptyList());

        uut.refresh();

        verify(dbManager).getUpdates();
        verify(serverManager).getUpdates();
        verify(dbManager).writeUpdates(Arrays.asList(array));
        verifyNoMoreInteractions(serverManager);
        verifyNoMoreInteractions(dbManager);
    }
}