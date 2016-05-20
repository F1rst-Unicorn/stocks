package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;

import java.util.List;
import java.util.Scanner;

public class UserCommand extends Command {

    public UserCommand(Configuration c) {
        command = "user";
        this.c = c;
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            listUsers();
        } else if (commands.get(1).equals("list")) {
            listUsers();
        } else if (commands.get(1).equals("add")) {
            addUser();
        } else {
            System.out.println("Unknown command: " + commands.get(1));
        }
    }

    public void listUsers() {
        User[] u = c.getDatabaseManager().getUsers();
        System.out.println("Current users: ");

        for (User user : u) {
            System.out.println("\t" + user.id + ": " + user.name);
        }
    }

    public void addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Creating a new user\nName: ");
        String name = scanner.next();
        User u = new User();
        u.name = name;
        c.getServerManager().addUser(u);

        (new RefreshCommand(c)).refreshUsers();
    }
}
