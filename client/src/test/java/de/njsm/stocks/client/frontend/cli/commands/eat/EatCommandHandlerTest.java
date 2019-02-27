package de.njsm.stocks.client.frontend.cli.commands.eat;

import de.njsm.stocks.client.business.data.FoodItem;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EatCommandHandlerTest {

    private EatCommandHandler uut;

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
        uut = new EatCommandHandler(server, writer, collector, refresher);
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
        when(collector.determineNextItem(input)).thenReturn(item);

        uut.handle(input);

        verify(collector).determineNextItem(input);
        verify(server).removeItem(item);
        verify(refresher).refresh();
    }
}