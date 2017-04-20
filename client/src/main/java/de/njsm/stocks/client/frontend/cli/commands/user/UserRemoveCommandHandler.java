package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.InputCollector;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.common.data.User;

public class UserRemoveCommandHandler extends AbstractCommandHandler {

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
    public void handle(Command command) {
        try {
            User userToRemove = inputCollector.determineUser(command);
            serverManager.removeUser(userToRemove);
            refresher.refresh();
        } catch (DatabaseException e) {
            logDatabaseError(e);
        } catch (NetworkException e) {
            logNetworkError(e);
        } catch (InputException e) {
            logInputError(e);
        }
    }
}
