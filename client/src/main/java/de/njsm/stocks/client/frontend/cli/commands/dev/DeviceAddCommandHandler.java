/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.business.data.ServerTicket;
import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.business.data.UserDevice;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.QrGenerator;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.List;

public class DeviceAddCommandHandler extends FaultyCommandHandler {

    private Refresher refresher;

    private InputCollector inputCollector;

    private DatabaseManager dbManager;

    private ServerManager serverManager;

    private Configuration configuration;

    private QrGenerator qrGenerator;

    public DeviceAddCommandHandler(Configuration configuration,
                                   ScreenWriter writer,
                                   Refresher refresher,
                                   InputCollector inputCollector,
                                   DatabaseManager dbManager,
                                   ServerManager serverManager,
                                   QrGenerator qrGenerator) {
        super(writer);
        this.command = "add";
        this.description = "Add a device";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.dbManager = dbManager;
        this.serverManager = serverManager;
        this.configuration = configuration;
        this.qrGenerator = qrGenerator;
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, InputException, NetworkException {
        User owner = inputCollector.determineUser(command);
        UserDevice deviceToAdd = inputCollector.createDevice(command, owner);
        if (inputCollector.confirm()) {
            addNewDevice(deviceToAdd);
        }
    }

    private void addNewDevice(UserDevice deviceToAdd) throws NetworkException, DatabaseException {
        ServerTicket ticket = serverManager.addDevice(deviceToAdd);
        refresher.refresh();
        UserDeviceView newDevice = getNewDeviceFromDatabase(deviceToAdd.name);
        printNewDevice(newDevice, ticket);
    }

    private UserDeviceView getNewDeviceFromDatabase(String deviceName) throws DatabaseException {
        List<UserDeviceView> devices = dbManager.getDevices(deviceName);
        return devices.get(devices.size()-1);
    }

    private void printNewDevice(UserDeviceView device, ServerTicket ticket) {
        writer.println("Creation successful. Enter parameters or scan QR code:");
        writer.println(generateQrCode(device, configuration, ticket));
        writer.println("\tHostname: " + configuration.getServerName());
        writer.println("\tCA port: " + configuration.getCaPort());
        writer.println("\tTicket port: " + configuration.getTicketPort());
        writer.println("\tServer port: " + configuration.getServerPort());
        writer.println("\tUser name: " + device.user);
        writer.println("\tDevice name: " + device.name);
        writer.println("\tUser ID: " + device.userId);
        writer.println("\tDevice ID: " + device.id);
        writer.println("\tFingerprint: " + configuration.getFingerprint());
        writer.println("\tTicket: " + ticket.ticket);
    }

    private String generateQrCode(UserDeviceView device, Configuration configuration, ServerTicket ticket) {
        return qrGenerator.generateQrCode(device.user + "\n" +
                device.name + "\n" +
                device.userId + "\n" +
                device.id + "\n" +
                configuration.getFingerprint() + "\n" +
                ticket.ticket + "\n" +
                configuration.getServerName() + "\n" +
                configuration.getCaPort() + "\n" +
                configuration.getTicketPort() + "\n" +
                configuration.getServerPort());
    }
}
