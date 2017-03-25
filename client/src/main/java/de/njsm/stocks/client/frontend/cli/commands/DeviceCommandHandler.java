package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.CommandManager;
import de.njsm.stocks.common.data.view.UserDeviceView;
import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.frontend.cli.EnhancedInputReader;

import java.util.LinkedList;
import java.util.List;

public class DeviceCommandHandler extends AbstractCommandHandler {

    protected final CommandManager m;

    public DeviceCommandHandler(Configuration c) {
        this.c = c;
        this.command = "dev";
        this.description = "Manage the devices accessing the stocks system";

        List<AbstractCommandHandler> commandList = new LinkedList<>();
        commandList.add(new DeviceAddCommandHandler(c));
        commandList.add(new DeviceListCommandHandler(c));
        commandList.add(new DeviceRemoveCommandHandler(c));
        this.m = new CommandManager(commandList, command);
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            m.handleCommand(command);
        } else {
            new DeviceListCommandHandler(c).handle(command);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectDevice(List<UserDeviceView> d, String name) throws SelectException {
        InputReader scanner = new EnhancedInputReader(System.in);
        int result;

        if (d.size() == 1) {
            result = d.get(0).id;
        } else if (d.size() == 0) {
            throw new SelectException("No such device found: " + name);
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
