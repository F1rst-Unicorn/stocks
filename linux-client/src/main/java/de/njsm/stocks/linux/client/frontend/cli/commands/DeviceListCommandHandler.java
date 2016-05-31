package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;

import java.util.List;

public class DeviceListCommandHandler extends CommandHandler {

    public DeviceListCommandHandler(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List all the devices";
    }

    @Override
    public void handle(List<String> commands) {
        listDevices();
    }

    public void listDevices() {
        UserDeviceView[] devices = c.getDatabaseManager().getDevices();
        System.out.println("Current devices: ");

        for (UserDeviceView dev : devices) {
            System.out.println("\t" + dev.id + ": " + dev.user + "'s " + dev.name);
        }
    }

}
