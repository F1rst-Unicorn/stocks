package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.commands.move.MoveCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MoveCommandHandlerTest {

    private MoveCommandHandler uut;

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
        uut = new MoveCommandHandler(server, collector, writer, refresher);
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
        FoodItem item = new FoodItem();
        Location location = new Location(2, "Fridge");
        Command input = Command.createCommand(new String[0]);
        when(collector.determineItem(input)).thenReturn(item);
        when(collector.determineDestinationLocation(input)).thenReturn(location);

        uut.handle(input);

        verify(collector).determineItem(input);
        verify(collector).determineDestinationLocation(input);
        verify(server).move(item, location.id);
        verify(refresher).refresh();
    }

    @Test
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }
}