package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.UserDevice;

public class DeviceRemoveCommandHandler extends AbstractCommandHandler {

    private Refresher refresher;

    private InputCollector inputCollector;

    private ServerManager serverManager;

    public DeviceRemoveCommandHandler(ScreenWriter writer,
                                      Refresher refresher,
                                      InputCollector inputCollector,
                                      ServerManager serverManager) {
        super(writer);
        this.command = "remove";
        this.description = "Remove a device";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        try {
            UserDevice deviceToRemove = inputCollector.determineDevice(command);
            serverManager.removeDevice(deviceToRemove);
            refresher.refresh();
        } catch (InputException e) {
            logInputError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
        }
    }
}
