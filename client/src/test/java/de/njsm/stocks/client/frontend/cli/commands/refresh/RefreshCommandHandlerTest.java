package de.njsm.stocks.client.frontend.cli.commands.refresh;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RefreshCommandHandlerTest {

    private RefreshCommandHandler uut;

    private Refresher refresher;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        refresher = mock(Refresher.class);
        writer = mock(ScreenWriter.class);
        uut = new RefreshCommandHandler(writer, refresher);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(refresher);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void handleSparseRefresh() throws Exception {
        when(refresher.refresh()).thenReturn(false);

        uut.handle(Command.createCommand(new String[0]));

        verify(refresher).refresh();
        verify(writer).println("Update successful");
    }

    @Test
    public void handleFullRefresh() throws Exception {

        uut.handle(Command.createCommand("-f"));

        verify(refresher).refreshFull();
    }

    @Test
    public void sparseRefreshWithoutChangePrintsDifferentMessage() throws Exception {
        when(refresher.refresh()).thenReturn(true);

        uut.handle(Command.createCommand(new String[0]));

        verify(refresher).refresh();
        verify(writer).println("Already up to date");
    }

    @Test
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }
}