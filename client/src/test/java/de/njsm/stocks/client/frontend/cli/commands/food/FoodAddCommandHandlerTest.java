package de.njsm.stocks.client.frontend.cli.commands.food;

import de.njsm.stocks.client.business.data.Food;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FoodAddCommandHandlerTest {

    private FoodAddCommandHandler uut;

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
        uut = new FoodAddCommandHandler(writer, refresher, collector, server);
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
        Food item = new Food();
        Command input = Command.createCommand(new String[0]);
        when(collector.createFood(input)).thenReturn(item);

        uut.handle(input);

        verify(collector).createFood(input);
        verify(server).addFood(item);
        verify(refresher).refresh();
    }
}