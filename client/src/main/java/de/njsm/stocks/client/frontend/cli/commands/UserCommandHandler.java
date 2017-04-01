package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.*;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.client.exceptions.InputException;

import java.util.LinkedList;
import java.util.List;

public class UserCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public UserCommandHandler(Configuration c, ScreenWriter writer) {
        super(writer);
        command = "user";
        description = "Manage the users of the stocks system";
        this.c = c;

        List<AbstractCommandHandler> commandList = new LinkedList<>();
        commandList.add(new UserAddCommandHandler(c, writer));
        commandList.add(new UserListCommandHandler(c, writer));
        commandList.add(new UserRemoveCommandHandler(c, writer));
        m = new CommandManager(commandList, "user");
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new UserListCommandHandler(c, writer).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectUser(List<User> users, String name) throws InputException {
        InputReader scanner = new InputReader(System.in);
        int result;
        if (users.size() == 1) {
            result = users.get(0).id;
        } else if (users.size() == 0) {
            throw new InputException("No such user found: " + name);
        } else {
            System.out.println("Several users found");
            for (User u : users) {
                System.out.println("\t" + u.id + ": " + u.name);
            }
            result = scanner.nextInt("Choose one ", users.get(0).id);
        }
        return result;
    }
}
