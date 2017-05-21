package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class UserListCommandHandlerTest {

    private UserListCommandHandler uut;

    private DatabaseManager dbManager;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        dbManager = mock(DatabaseManager.class);
        writer = mock(ScreenWriter.class);
        uut = new UserListCommandHandler(writer, dbManager);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(dbManager);
    }

    @Test
    public void handlingWorks() throws Exception {
        when(dbManager.getUsers()).thenReturn(Collections.emptyList());

        uut.handle(null);

        verify(dbManager).getUsers();
        verify(writer).printUsers("Current users: ", Collections.emptyList());
    }
}