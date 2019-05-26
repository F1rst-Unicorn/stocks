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

import de.njsm.stocks.client.business.data.Update;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class RefresherTest {

    private Refresher uut;

    private DatabaseManager dbManager;

    private ServerManager serverManager;

    @Before
    public void setup() throws Exception {
        serverManager = mock(ServerManager.class);
        dbManager = mock(DatabaseManager.class);
        uut = new Refresher(serverManager, dbManager);
    }

    @Test
    public void fullRefreshResetsDatabaseTable() throws Exception {
        when(serverManager.getUpdates()).thenReturn(Collections.emptyList());
        when(dbManager.getUpdates()).thenReturn(Collections.emptyList());

        uut.refreshFull();

        verify(dbManager).resetUpdates();
    }

    @Test
    public void noUpdateOnEqualDate() throws Exception {
        Update u = new Update("foo", Instant.now());
        List<Update> updateList = Collections.singletonList(u);
        when(serverManager.getUpdates()).thenReturn(Collections.singletonList(u));
        when(dbManager.getUpdates()).thenReturn(updateList);

        uut.refresh();

        verify(serverManager).getUpdates();
        verify(dbManager).getUpdates();
        verify(dbManager).writeUpdates(updateList);
        verifyNoMoreInteractions(serverManager);
        verifyNoMoreInteractions(dbManager);
    }

    @Test
    public void ignoreUnknownTables() throws Exception {
        Update u = new Update("foo", Instant.now());
        when(serverManager.getUpdates()).thenReturn(Collections.singletonList(u));
        when(dbManager.getUpdates()).thenReturn(Collections.emptyList());

        uut.refresh();

        verify(serverManager).getUpdates();
        verify(dbManager).getUpdates();
        verify(dbManager).writeUpdates(Collections.singletonList(u));
        verifyNoMoreInteractions(serverManager);
        verifyNoMoreInteractions(dbManager);
    }
}