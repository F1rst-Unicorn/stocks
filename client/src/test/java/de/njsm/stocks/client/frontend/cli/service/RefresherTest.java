package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Update;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
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
        when(serverManager.getUpdates()).thenReturn(new Update[0]);
        when(dbManager.getUpdates()).thenReturn(Collections.emptyList());

        uut.refreshFull();

        verify(dbManager).resetUpdates();
    }

    @Test
    public void noUpdateOnEqualDate() throws Exception {
        Update u = new Update("foo", new Date());
        List<Update> updateList = Collections.singletonList(u);
        when(serverManager.getUpdates()).thenReturn(new Update[] {u});
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
        Update u = new Update("foo", new Date());
        when(serverManager.getUpdates()).thenReturn(new Update[] {u});
        when(dbManager.getUpdates()).thenReturn(Collections.emptyList());

        uut.refresh();

        verify(serverManager).getUpdates();
        verify(dbManager).getUpdates();
        verify(dbManager).writeUpdates(Collections.singletonList(u));
        verifyNoMoreInteractions(serverManager);
        verifyNoMoreInteractions(dbManager);
    }
}