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