package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.User;

import java.util.List;

public class UserListCommandHandler extends AbstractCommandHandler {

    private DatabaseManager dbManager;

    public UserListCommandHandler(ScreenWriter writer, DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List the users";
        this.dbManager = dbManager;
    }

    @Override
    public void handle(Command command) {
        try {
            List<User> u = dbManager.getUsers();
            writer.printUsers("Current users: ", u);
        } catch (DatabaseException e)  {
            e.printStackTrace();
        }
    }
}
