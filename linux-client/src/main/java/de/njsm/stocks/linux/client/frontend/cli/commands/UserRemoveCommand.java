package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class UserRemoveCommand extends Command {

    public UserRemoveCommand(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a user";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            removeUser(commands.get(0));
        } else {
            removeUser();
        }
    }

    public void removeUser() {
        System.out.print("Remove a user\nName: ");
        String name = c.getReader().next();
        removeUser(name);
    }

    public void removeUser(String name) {
        try {
            User[] users = c.getDatabaseManager().getUsers(name);
            int id = UserCommand.selectUser(users, name);

            for (User u : users) {
                if (u.id == id) {
                    c.getServerManager().removeUser(u);
                    (new RefreshCommand(c)).refreshUsers();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
