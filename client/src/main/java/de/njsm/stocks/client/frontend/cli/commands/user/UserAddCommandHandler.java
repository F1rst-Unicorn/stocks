package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.User;

public class UserAddCommandHandler extends AbstractCommandHandler {

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
    public void handle(Command command) {
        try {
            User userToAdd = inputCollector.resolveNewUser(command);
            serverManager.addUser(userToAdd);
            refresher.refresh();
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
        }
    }
}
