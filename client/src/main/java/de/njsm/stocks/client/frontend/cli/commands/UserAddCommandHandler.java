package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.client.frontend.cli.InputReader;

public class UserAddCommandHandler extends AbstractCommandHandler {

    public UserAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new user";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            addUser(command.next());
        } else {
            addUser();
        }
    }

    public void addUser() {
        String name = c.getReader().nextName("Creating a new user\nName: ");
        addUser(name);
    }

    public void addUser(String name) {
        try {
            User u = new User();
            u.name = name;

            if (!InputReader.isNameValid(name)) {
                addUser();
                return;
            }

            c.getServerManager().addUser(u);

            (new RefreshCommandHandler(c, false)).refresh();
        } catch (NetworkException e) {
            // TODO LOG
        }
    }
}
