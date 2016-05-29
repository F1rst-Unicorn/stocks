package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.LinkedList;
import java.util.List;

public class DeviceCommand extends Command {

    protected final CommandManager m;

    public DeviceCommand(Configuration c) {
        this.c = c;
        this.command = "dev";
        this.description = "Manage the devices accessing the stocks system";

        List<Command> commandList = new LinkedList<>();
        commandList.add(new DeviceAddCommand(c));
        commandList.add(new DeviceListCommand(c));
        commandList.add(new DeviceRemoveCommand(c));
        this.m = new CommandManager(commandList, command);
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.isEmpty()) {
            new DeviceListCommand(c).listDevices();
        } else {
            m.handleCommand(commands);
        }
    }

    @Override
    public void printHelp() {
        m.printHelp();
    }

    public static int selectDevice(UserDeviceView[] d, String name) throws SelectException {
        InputReader scanner = new InputReader(System.in);
        int result;

        if (d.length == 1) {
            result = d[0].id;
        } else if (d.length == 0) {
            throw new SelectException("No such device found: " + name);
        } else {
            System.out.println("Several devices found");
            for (UserDeviceView dev : d) {
                System.out.println("\t" + dev.id + ": " + dev.user + "'s " + dev.name);
            }
            System.out.print("Choose one (default " + d[0].id + "): ");
            result = scanner.nextInt(d[0].id);
        }
        return result;
    }


}
