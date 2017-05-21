package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.common.data.UserDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DeviceRemoveCommandHandlerTest {

    private DeviceRemoveCommandHandler uut;

    private ScreenWriter writer;

    private Refresher refresher;

    private InputCollector collector;

    private ServerManager server;

    @Before
    public void setup() throws Exception {
        collector = mock(InputCollector.class);
        server = mock(ServerManager.class);
        refresher = mock(Refresher.class);
        writer = mock(ScreenWriter.class);
        uut = new DeviceRemoveCommandHandler(writer, refresher, collector, server);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(collector);
        verifyNoMoreInteractions(server);
        verifyNoMoreInteractions(refresher);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void handlingWorks() throws Exception {
        UserDevice item = new UserDevice();
        Command input = Command.createCommand(new String[0]);
        when(collector.determineDevice(input)).thenReturn(item);

        uut.handle(input);

        verify(collector).determineDevice(input);
        verify(server).removeDevice(item);
        verify(refresher).refresh();
    }
}