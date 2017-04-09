package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;

import java.util.List;

public class DeviceAddCommandHandler extends AbstractCommandHandler {

    private Refresher refresher;

    private InputCollector inputCollector;

    private DatabaseManager dbManager;

    private ServerManager serverManager;

    private Configuration configuration;

    public DeviceAddCommandHandler(Configuration configuration,
                                   ScreenWriter writer,
                                   Refresher refresher,
                                   InputCollector inputCollector,
                                   DatabaseManager dbManager,
                                   ServerManager serverManager) {
        super(writer);
        this.command = "add";
        this.description = "Add a device";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.dbManager = dbManager;
        this.serverManager = serverManager;
        this.configuration = configuration;
    }

    @Override
    public void handle(Command command) {
        try {
            handleInternally(command);
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (InputException e) {
            logInputError(e);
        }
    }

    private void handleInternally(Command command) throws DatabaseException, InputException, NetworkException {
        User owner = inputCollector.determineUser(command);
        UserDevice deviceToAdd = inputCollector.determineNewDevice(command, owner);
        if (inputCollector.confirm()) {
            addNewDevice(deviceToAdd);
        }
    }

    private void addNewDevice(UserDevice deviceToAdd) throws NetworkException, DatabaseException {
        Ticket ticket = serverManager.addDevice(deviceToAdd);
        refresher.refresh();
        UserDeviceView newDevice = getNewDeviceFromDatabase(deviceToAdd.name);
        printNewDevice(newDevice, ticket);
    }

    private UserDeviceView getNewDeviceFromDatabase(String deviceName) throws DatabaseException {
        List<UserDeviceView> devices = dbManager.getDevices(deviceName);
        return devices.get(devices.size()-1);
    }

    private void printNewDevice(UserDeviceView device, Ticket ticket) throws DatabaseException {
        writer.println("Creation successful. The new device needs these parameters:");
        writer.println("\tUser name: " + device.user);
        writer.println("\tDevice name: " + device.name);
        writer.println("\tUser ID: " + device.userId);
        writer.println("\tDevice ID: " + device.id);
        writer.println("\tFingerprint: " + configuration.getFingerprint());
        writer.println("\tTicket: " + ticket.ticket);
    }
}
