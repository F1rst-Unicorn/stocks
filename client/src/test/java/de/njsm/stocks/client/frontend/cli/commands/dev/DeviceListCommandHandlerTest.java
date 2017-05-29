package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class DeviceListCommandHandlerTest {

    private DeviceListCommandHandler uut;

    private DatabaseManager dbManager;

    private ScreenWriter writer;

    @Before
    public void setup() throws Exception {
        dbManager = mock(DatabaseManager.class);
        writer = mock(ScreenWriter.class);
        uut = new DeviceListCommandHandler(writer, dbManager);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(dbManager);
    }

    @Test
    public void handlingWorks() throws Exception {
        when(dbManager.getDevices()).thenReturn(Collections.emptyList());

        uut.handle(null);

        verify(dbManager).getDevices();
        verify(writer).printUserDeviceViews("Current devices: ", Collections.emptyList());
    }
}