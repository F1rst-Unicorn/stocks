package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.client.exceptions.DatabaseException;

import java.util.List;

public class UserListCommandHandler extends AbstractCommandHandler {

    public UserListCommandHandler(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List the users";
    }

    @Override
    public void handle(Command command) {
        listUsers();
    }

    public void listUsers() {
        try {
            List<User> u = c.getDatabaseManager().getUsers();
            System.out.println("Current users: ");

            for (User user : u) {
                System.out.println("\t" + user.id + ": " + user.name);
            }
        } catch (DatabaseException e)  {
            e.printStackTrace();
        }
    }

}
