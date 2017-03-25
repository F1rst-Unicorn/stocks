package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;

import java.util.List;

public class DeviceAddCommandHandler extends AbstractCommandHandler {

    public DeviceAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a device";
    }

    @Override
    public void handle(Command command) {
        String devName;
        String userName;

        if (command.hasNext()) {
            devName = command.next();
            if (command.hasNext()) {
                userName = command.next();
                addDevice(devName, userName);
            } else {
                addDevice();
            }
        } else {
            addDevice();
        }
    }

    public void addDevice() {
        String name = c.getReader().nextName("Creating a new device\nName: ");
        String user = c.getReader().next("Who is the owner?  ");
        addDevice(name, user);
    }

    public void addDevice(String name, String username) {
        try {
            int userId = UserCommandHandler.selectUser(
                    c.getDatabaseManager().getUsers(username),
                    username);
            Ticket ticket;


            System.out.print("Create new device '" + name + "' for user '" +
                    username + "'? [y/N]  ");
            if (c.getReader().getYesNo()) {
                UserDevice d = new UserDevice();
                d.name = name;
                d.userId = userId;
                try {
                    ticket = c.getServerManager().addDevice(d);
                    (new RefreshCommandHandler(c, false)).refresh();

                    List<UserDeviceView> devices = c.getDatabaseManager().getDevices(d.name);

                    System.out.println("Creation successful. The new device needs these parameters:");
                    System.out.println("\tUser name: " + username);
                    System.out.println("\tDevice name: " + name);
                    System.out.println("\tUser ID: " + userId);
                    System.out.println("\tDevice ID: " + devices.get(devices.size()-1).id);
                    System.out.println("\tFingerprint: " + c.getFingerprint());
                    System.out.println("\tTicket: " + ticket.ticket);
                } catch (RuntimeException e) {
                    System.out.println("Creation failed. " + e.getMessage());
                }
            } else {
                System.out.println("Aborted.");
            }
        } catch (SelectException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }


    }
}
