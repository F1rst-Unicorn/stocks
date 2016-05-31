package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;

import java.util.List;

public class UserListCommandHandler extends CommandHandler {

    public UserListCommandHandler(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List the users";
    }

    @Override
    public void handle(List<String> commands) {
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
