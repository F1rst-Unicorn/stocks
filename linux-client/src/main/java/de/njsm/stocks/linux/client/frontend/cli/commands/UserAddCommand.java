package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class UserAddCommand extends Command {

    public UserAddCommand(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new user";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            addUser(commands.get(0));
        } else {
            addUser();
        }
    }

    public void addUser() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Creating a new user\nName: ");
        String name = scanner.nextName();
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

        (new RefreshCommand(c)).refreshUsers();
    }
}
