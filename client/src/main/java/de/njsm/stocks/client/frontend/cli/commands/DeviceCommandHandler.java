package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.CommandManager;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;

import java.util.LinkedList;
import java.util.List;

public class DeviceCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public DeviceCommandHandler(Configuration c, ScreenWriter writer, Selector selector) {
        super(writer);
        this.c = c;
        this.command = "dev";
        this.description = "Manage the devices accessing the stocks system";

        List<AbstractCommandHandler> commandList = new LinkedList<>();
        commandList.add(new DeviceAddCommandHandler(c, writer, selector));
        commandList.add(new DeviceListCommandHandler(c, writer));
        commandList.add(new DeviceRemoveCommandHandler(c, writer, selector));
        this.m = new CommandManager(commandList, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new DeviceListCommandHandler(c, writer).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }
}
