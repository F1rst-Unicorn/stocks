package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.data.User;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.frontend.cli.EnhancedInputReader;
import de.njsm.stocks.client.frontend.cli.InputReader;

import java.util.LinkedList;
import java.util.List;

public class UserCommandHandler extends CommandHandler {

    protected final CommandManager m;

    public UserCommandHandler(Configuration c) {
        command = "user";
        description = "Manage the users of the stocks system";
        this.c = c;

        List<CommandHandler> commandList = new LinkedList<>();
        commandList.add(new UserAddCommandHandler(c));
        commandList.add(new UserListCommandHandler(c));
        commandList.add(new UserRemoveCommandHandler(c));
        m = new CommandManager(commandList, "user");
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new UserListCommandHandler(c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectUser(User[] users, String name) throws SelectException {
        InputReader scanner = new EnhancedInputReader(System.in);
        int result;
        if (users.length == 1) {
            result = users[0].id;
        } else if (users.length == 0) {
            throw new SelectException("No such user found: " + name);
        } else {
            System.out.println("Several users found");
            for (User u : users) {
                System.out.println("\t" + u.id + ": " + u.name);
            }
            result = scanner.nextInt("Choose one ", users[0].id);
        }
        return result;
    }
}
