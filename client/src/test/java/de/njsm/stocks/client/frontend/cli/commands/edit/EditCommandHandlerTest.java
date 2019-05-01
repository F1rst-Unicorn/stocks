package de.njsm.stocks.client.frontend.cli.commands.edit;

import de.njsm.stocks.client.business.data.FoodItem;
import de.njsm.stocks.client.business.data.Location;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

public class EditCommandHandlerTest {

    private EditCommandHandler uut;

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
        uut = new EditCommandHandler(server, collector, writer, refresher);
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
        Location location = new Location(2, 7, "Fridge");
        Instant newEatBy = Instant.now();
        item.eatByDate = newEatBy;
        Command input = Command.createCommand(new String[0]);
        when(collector.determineItem(input)).thenReturn(item);
        when(collector.determineDestinationLocation(input)).thenReturn(location);
        when(collector.determineDate(input, newEatBy)).thenReturn(newEatBy);

        uut.handle(input);

        verify(collector).determineItem(input);
        verify(collector).determineDestinationLocation(input);
        verify(collector).determineDate(input, newEatBy);
        verify(server).edit(item, newEatBy, location.id);
        verify(refresher).refresh();
    }

    @Test
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }
}