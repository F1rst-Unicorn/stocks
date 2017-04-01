package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.User;

import java.util.List;

public class UserRemoveCommandHandler extends AbstractCommandHandler {

    private Selector selector;

    public UserRemoveCommandHandler(Configuration c, ScreenWriter writer, Selector selector) {
        super(writer);
        this.c = c;
        this.command = "remove";
        this.description = "Remove a user";
        this.selector = selector;
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            removeUser(command.next());
        } else {
            removeUser();
        }
    }

    public void removeUser() {
        String name = c.getReader().next("Remove a user\nName: ");
        removeUser(name);
    }

    public void removeUser(String name) {
        try {
            List<User> users = c.getDatabaseManager().getUsers(name);
            User user = selector.selectUser(users, name);

            for (User u : users) {
                if (u.id == user.id) {
                    c.getServerManager().removeUser(u);
                    (new RefreshCommandHandler(c, writer, false)).refresh();
                }
            }
        } catch (InputException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }
    }
}
