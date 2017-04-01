package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.CommandManager;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;

import java.util.LinkedList;
import java.util.List;

public class UserCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public UserCommandHandler(Configuration c, ScreenWriter writer, Selector selector) {
        super(writer);
        command = "user";
        description = "Manage the users of the stocks system";
        this.c = c;

        List<AbstractCommandHandler> commandList = new LinkedList<>();
        commandList.add(new UserAddCommandHandler(c, writer));
        commandList.add(new UserListCommandHandler(c, writer));
        commandList.add(new UserRemoveCommandHandler(c, writer, selector));
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

}
