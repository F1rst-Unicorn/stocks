package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

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
            if (commands.size() == 3){
                addUser(commands.get(2));
            } else {
                addUser();
            }
        } else if (commands.get(1).equals("remove")) {
            if (commands.size() == 3){
                removeUser(commands.get(2));
            } else {
                removeUser();
            }
        } else {
            System.out.println("Unknown command: " + commands.get(1));
        }
    }

    @Override
    public void printHelp() {

    }

    public void listUsers() {
        User[] u = c.getDatabaseManager().getUsers();
        System.out.println("Current users: ");

        for (User user : u) {
            System.out.println("\t" + user.id + ": " + user.name);
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

    public void removeUser() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove a user\nName: ");
        String name = scanner.next();
        removeUser(name);
    }

    public void removeUser(String name) {
        InputReader scanner = new InputReader(System.in);
        User[] users = c.getDatabaseManager().getUsers(name);
        int id;

        if (users.length == 1) {
            id = users[0].id;
        } else if (users.length == 0) {
            System.out.println("No such user found: " + name);
            return;
        } else {
            System.out.println("Several users found");
            for (User u : users) {
                System.out.println("\t" + u.id + ": " + u.name);
            }
            System.out.print("Choose one (default " + users[0].id + "): ");
            id = scanner.nextInt(users[0].id);
        }

        for (User u : users) {
            if (u.id == id){
                c.getServerManager().removeUser(u);
            }
        }

        (new RefreshCommand(c)).refreshUsers();
    }
}
