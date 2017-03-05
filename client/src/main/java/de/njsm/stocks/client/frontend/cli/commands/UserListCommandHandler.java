package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.User;

public class UserListCommandHandler extends CommandHandler {

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
        User[] u = c.getDatabaseManager().getUsers();
        System.out.println("Current users: ");

        for (User user : u) {
            System.out.println("\t" + user.id + ": " + user.name);
        }
    }

}
