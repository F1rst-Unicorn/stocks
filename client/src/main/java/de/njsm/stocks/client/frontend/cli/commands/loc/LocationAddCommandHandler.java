package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.Location;

public class LocationAddCommandHandler extends AbstractCommandHandler {

    private Refresher refresher;

    private InputCollector inputCollector;

    private ServerManager serverManager;

    public LocationAddCommandHandler(ScreenWriter writer, Refresher refresher, InputCollector inputCollector, ServerManager serverManager) {
        super(writer);
        this.command = "add";
        this.description = "Add a new food location to the system";
        this.refresher = refresher;
        this.inputCollector = inputCollector;
        this.serverManager = serverManager;
    }

    @Override
    public void handle(Command command) {
        try {
            Location location = inputCollector.createLocation(command);
            serverManager.addLocation(location);
            refresher.refresh();
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}
