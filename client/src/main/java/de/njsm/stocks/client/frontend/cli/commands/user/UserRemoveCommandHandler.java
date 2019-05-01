package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.service.Refresher;

public class UserRemoveCommandHandler extends FaultyCommandHandler {

    private final ServerManager serverManager;

    private InputCollector inputCollector;

    private Refresher refresher;

    public UserRemoveCommandHandler(ScreenWriter writer, ServerManager serverManager, InputCollector inputCollector, Refresher refresher) {
        super(writer);
        this.command = "remove";
        this.description = "Remove a user";
        this.inputCollector = inputCollector;
        this.refresher = refresher;
        this.serverManager = serverManager;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        User userToRemove = inputCollector.determineUser(command);
        serverManager.removeUser(userToRemove);
        refresher.refresh();

    }
}
