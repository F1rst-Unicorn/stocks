package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.business.data.Location;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LocationRenameCommandHandlerTest {

    private LocationRenameCommandHandler uut;

    private InputCollector collector;

    private ServerManager server;

    private Refresher refresher;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        collector = mock(InputCollector.class);
        server = mock(ServerManager.class);
        refresher = mock(Refresher.class);
        writer = mock(ScreenWriter.class);
        uut = new LocationRenameCommandHandler(writer, collector, refresher, server);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(collector);
        verifyNoMoreInteractions(server);
        verifyNoMoreInteractions(refresher);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void handlesWorks() throws Exception {
        String newName = "Beer";
        String prompt = "New name: ";
        Location item = new Location();
        Command input = Command.createCommand(new String[0]);
        when(collector.determineLocation(input)).thenReturn(item);
        when(collector.determineNameFromCommandOrAsk(prompt, input)).thenReturn(newName);

        uut.handle(input);

        verify(collector).determineLocation(input);
        verify(collector).determineNameFromCommandOrAsk(prompt, input);
        verify(server).renameLocation(item, newName);
        verify(refresher).refresh();
    }
}