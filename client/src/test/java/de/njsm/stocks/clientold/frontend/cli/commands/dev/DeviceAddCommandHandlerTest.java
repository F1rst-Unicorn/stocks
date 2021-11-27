/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.frontend.cli.commands.dev;

import de.njsm.stocks.clientold.business.data.ServerTicket;
import de.njsm.stocks.clientold.business.data.User;
import de.njsm.stocks.clientold.business.data.UserDevice;
import de.njsm.stocks.clientold.business.data.view.UserDeviceView;
import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.frontend.cli.Command;
import de.njsm.stocks.clientold.frontend.cli.commands.InputCollector;
import de.njsm.stocks.clientold.frontend.cli.service.QrGenerator;
import de.njsm.stocks.clientold.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.clientold.network.server.ServerManager;
import de.njsm.stocks.clientold.service.Refresher;
import de.njsm.stocks.clientold.storage.DatabaseManager;
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
        String serverName = "serverName";
        int caPort = 10910;
        int ticketPort = 10911;
        int serverPort = 10912;
        String fingerPrint = "00:11:22:33";
        String qrFake = "this might be a qr code";
        User user = new User(2, 7, "Jack");
        UserDevice item = new UserDevice(3, 8, "Mobile", user.id);
        ServerTicket ticket = new ServerTicket(item.id, "some ticket");
        Command input = Command.createCommand(new String[0]);
        when(collector.determineUser(input)).thenReturn(user);
        when(collector.createDevice(input, user)).thenReturn(item);
        when(collector.confirm()).thenReturn(true);
        when(configuration.getServerName()).thenReturn(serverName);
        when(configuration.getCaPort()).thenReturn(caPort);
        when(configuration.getTicketPort()).thenReturn(ticketPort);
        when(configuration.getServerPort()).thenReturn(serverPort);
        when(configuration.getFingerprint()).thenReturn(fingerPrint);
        when(server.addDevice(item)).thenReturn(ticket);
        when(qrGenerator.generateQrCode(any())).thenReturn(qrFake);
        when(dbManager.getDevices(item.name)).thenReturn(Collections.singletonList(
                new UserDeviceView(item.id, item.version, item.name, user.name, user.id)));

        uut.handle(input);

        verify(collector).createDevice(input, user);
        verify(collector).determineUser(input);
        verify(collector).confirm();
        verify(server).addDevice(item);
        verify(refresher).refresh();
        verify(dbManager).getDevices(item.name);
        verify(configuration, times(2)).getFingerprint();
        verify(configuration, times(2)).getServerName();
        verify(configuration, times(2)).getCaPort();
        verify(configuration, times(2)).getTicketPort();
        verify(configuration, times(2)).getServerPort();
        verify(qrGenerator).generateQrCode(any());
        verify(writer).println("Creation successful. Enter parameters or scan QR code:");
        verify(writer).println(qrFake);
        verify(writer).println("\tHostname: " + serverName);
        verify(writer).println("\tCA port: " + caPort);
        verify(writer).println("\tTicket port: " + ticketPort);
        verify(writer).println("\tServer port: " + serverPort);
        verify(writer).println("\tUser name: " + user.name);
        verify(writer).println("\tDevice name: " + item.name);
        verify(writer).println("\tUser ID: " + user.id);
        verify(writer).println("\tDevice ID: " + item.id);
        verify(writer).println("\tFingerprint: " + fingerPrint);
        verify(writer).println("\tTicket: " + ticket.ticket);
    }

    @Test
    public void noConfirmationAborts() throws Exception {
        User user = new User(2, 7, "Jack");
        UserDevice item = new UserDevice(3, 8, "Mobile", user.id);
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
