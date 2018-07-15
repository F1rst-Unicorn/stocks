package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.QrGenerator;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class DeviceAddCommandHandlerTest {

    private DeviceAddCommandHandler uut;

    private ScreenWriter writer;

    private Refresher refresher;

    private InputCollector collector;

    private ServerManager server;

    private DatabaseManager dbManager;

    private Configuration configuration;

    private QrGenerator qrGenerator;

    @Before
    public void setup() throws Exception {
        collector = mock(InputCollector.class);
        server = mock(ServerManager.class);
        refresher = mock(Refresher.class);
        writer = mock(ScreenWriter.class);
        dbManager = mock(DatabaseManager.class);
        configuration = mock(Configuration.class);
        qrGenerator = mock(QrGenerator.class);
        uut = new DeviceAddCommandHandler(configuration, writer, refresher, collector, dbManager, server, qrGenerator);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(collector);
        verifyNoMoreInteractions(server);
        verifyNoMoreInteractions(refresher);
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(dbManager);
        verifyNoMoreInteractions(configuration);
        verifyNoMoreInteractions(qrGenerator);
    }

    @Test
    public void handlingWorks() throws Exception {
        String fingerPrint = "00:11:22:33";
        String qrFake = "this might be a qr code";
        User user = new User(2, "Jack");
        UserDevice item = new UserDevice(3, "Mobile", user.id);
        Ticket ticket = new Ticket(item.id, "some ticket", "some fake PEM");
        Command input = Command.createCommand(new String[0]);
        when(collector.determineUser(input)).thenReturn(user);
        when(collector.createDevice(input, user)).thenReturn(item);
        when(collector.confirm()).thenReturn(true);
        when(configuration.getFingerprint()).thenReturn(fingerPrint);
        when(server.addDevice(item)).thenReturn(ticket);
        when(qrGenerator.generateQrCode(any())).thenReturn(qrFake);
        when(dbManager.getDevices(item.name)).thenReturn(Collections.singletonList(
                new UserDeviceView(item.id, item.name, user.name, user.id)));

        uut.handle(input);

        verify(collector).createDevice(input, user);
        verify(collector).determineUser(input);
        verify(collector).confirm();
        verify(server).addDevice(item);
        verify(refresher).refresh();
        verify(dbManager).getDevices(item.name);
        verify(configuration, times(2)).getFingerprint();
        verify(qrGenerator).generateQrCode(any());
        verify(writer).println("Creation successful. Enter parameters or scan QR code:");
        verify(writer).println(qrFake);
        verify(writer).println("\tUser name: " + user.name);
        verify(writer).println("\tDevice name: " + item.name);
        verify(writer).println("\tUser ID: " + user.id);
        verify(writer).println("\tDevice ID: " + item.id);
        verify(writer).println("\tFingerprint: " + fingerPrint);
        verify(writer).println("\tTicket: " + ticket.ticket);
    }

    @Test
    public void noConfirmationAborts() throws Exception {
        User user = new User(2, "Jack");
        UserDevice item = new UserDevice(3, "Mobile", user.id);
        Command input = Command.createCommand(new String[0]);
        when(collector.determineUser(input)).thenReturn(user);
        when(collector.createDevice(input, user)).thenReturn(item);
        when(collector.confirm()).thenReturn(false);

        uut.handle(input);

        verify(collector).createDevice(input, user);
        verify(collector).determineUser(input);
        verify(collector).confirm();
    }
}