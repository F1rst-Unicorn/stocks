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

public class UserAddCommandHandler extends FaultyCommandHandler {

    private Refresher refresher;

    private ServerManager serverManager;

    private InputCollector inputCollector;

    public UserAddCommandHandler(ScreenWriter writer, ServerManager serverManager, InputCollector inputCollector, Refresher refresher) {
        super(writer);
        this.command = "add";
        this.description = "Add a new user";
        this.refresher = refresher;
        this.serverManager = serverManager;
        this.inputCollector = inputCollector;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        User userToAdd = inputCollector.createUser(command);
        serverManager.addUser(userToAdd);
        refresher.refresh();
    }
}
