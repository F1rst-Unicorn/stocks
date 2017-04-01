package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.*;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.view.UserDeviceView;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.InputException;

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
        commandList.add(new DeviceRemoveCommandHandler(c, writer));
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

    public static int selectDevice(List<UserDeviceView> d, String name) throws InputException {
        InputReader scanner = new InputReader(System.in);
        int result;

        if (d.size() == 1) {
            result = d.get(0).id;
        } else if (d.size() == 0) {
            throw new InputException("No such device found: " + name);
        } else {
            System.out.println("Several devices found");
            for (UserDeviceView dev : d) {
                System.out.println("\t" + dev.id + ": " + dev.user + "'s " + dev.name);
            }
            result = scanner.nextInt("Choose one ", d.get(0).id);
        }
        return result;
    }


}
