package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.Location;

public class LocationRenameCommandHandler extends AbstractCommandHandler {

    private InputCollector inputCollector;

    private Refresher refresher;

    private ServerManager serverManager;

    public LocationRenameCommandHandler(ScreenWriter writer, InputCollector inputCollector, Refresher refresher, ServerManager serverManager) {
        super(writer);
        this.command = "rename";
        this.description = "Rename a location";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
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
        Location location = inputCollector.determineLocation(command);
        String newName = inputCollector.determineNameFromCommandOrAsk("New name: ", command);
        serverManager.renameLocation(location, newName);
        refresher.refresh();
    }
}
