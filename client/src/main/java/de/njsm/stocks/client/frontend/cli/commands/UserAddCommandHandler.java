package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.data.User;
import de.njsm.stocks.client.frontend.cli.InputReader;

public class UserAddCommandHandler extends CommandHandler {

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
        User u = new User();
        u.name = name;

        if (! InputReader.isNameValid(name)){
            addUser();
            return;
        }

        c.getServerManager().addUser(u);

        (new RefreshCommandHandler(c, false)).refresh();
    }
}
