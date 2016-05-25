package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Ticket;
import de.njsm.stocks.linux.client.data.UserDevice;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class DeviceCommand extends Command {

    public DeviceCommand(Configuration c) {
        this.c = c;
        this.command = "dev";
        this.description = "Manage the devices accessing the stocks system";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1 ||
                commands.get(1).equals("list")) {
            listDevices();
        }else if (commands.get(1).equals("help")) {
            printHelp();
        } else if (commands.get(1).equals("add")) {
            if (commands.size() == 4){
                addDevice(commands.get(2), commands.get(3));
            } else {
                addDevice();
            }
        } else if (commands.get(1).equals("remove")) {
            if (commands.size() == 3){
                removeDevice(commands.get(2));
            } else {
                removeDevice();
            }
        } else {
            System.out.println("Unknown command: " + commands.get(1));
        }
    }

    @Override
    public void printHelp() {
        String help = "device command\n" +
                "\n" +
                "\thelp\t\t\tThis help screen\n" +
                "\tlist\t\t\tList the devices of the system\n" +
                "\tadd [name] [user]\t\tAdd a device to the system\n" +
                "\tremove [name]\t\tRemove a device from the system\n";
        System.out.println(help);
    }

    public void listDevices() {
        UserDeviceView[] devices = c.getDatabaseManager().getDevices();
        System.out.println("Current devices: ");

        for (UserDeviceView dev : devices) {
            System.out.println("\t" + dev.id + ": " + dev.user + "'s " + dev.name);
        }
    }

    public void addDevice() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Creating a new device\nName: ");
        String name = scanner.nextName();
        System.out.print("Who is the owner?  ");
        String user = scanner.next();
        addDevice(name, user);
    }

    public void addDevice(String name, String username) {
        InputReader scanner = new InputReader(System.in);
        int userId = UserCommand.selectUser(
                c.getDatabaseManager().getUsers(username),
                username);
        Ticket ticket;

        if (userId != -1) {
            System.out.print("Create new device '" + name + "' for user '" +
                    username + "'? [y/N]  ");
            if (scanner.getYesNo()) {
                UserDevice d = new UserDevice();
                d.name = name;
                d.userId = userId;
                try {
                    ticket = c.getServerManager().addDevice(d);
                    (new RefreshCommand(c)).refreshDevices();

                    System.out.println("Creation successful. The new device needs these parameters:");
                    System.out.println("\tUser name: " + username);
                    System.out.println("\tDevice name: " + name);
                    System.out.println("\tUser ID: " + userId);
                    System.out.println("\tDevice ID: ");
                    System.out.println("\tFingerprint: " + c.getFingerprint());
                    System.out.println("\tTicket: " + ticket.ticket);
                } catch (RuntimeException e) {
                    System.out.println("Creation failed. " + e.getMessage());
                }
            } else {
                System.out.println("Aborted.");
            }
        }


    }

    public void removeDevice() {

    }

    public void removeDevice(String name) {

    }
}
