package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.List;

public class UserListCommandHandler extends FaultyCommandHandler {

    private DatabaseManager dbManager;

    public UserListCommandHandler(ScreenWriter writer, DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List the users";
        this.dbManager = dbManager;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        List<User> u = dbManager.getUsers();
        writer.printUsers("Current users: ", u);

    }
}
