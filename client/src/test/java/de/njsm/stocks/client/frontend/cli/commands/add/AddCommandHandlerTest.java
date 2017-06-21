package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.common.data.FoodItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AddCommandHandlerTest {

    private AddCommandHandler uut;

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
        uut = new AddCommandHandler(collector, server, refresher, writer);
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
        Command input = Command.createCommand(new String[0]);
        when(collector.createFoodItem(input)).thenReturn(item);

        uut.handle(input);

        verify(collector).createFoodItem(input);
        verify(server).addItem(item);
        verify(refresher).refresh();
    }

    @Test
    public void additionalInputTriggersHelp() throws Exception {
        Command input = Command.createCommand("fdsafe");

        uut.handle(input);

        verify(writer).println(anyString());
    }
}