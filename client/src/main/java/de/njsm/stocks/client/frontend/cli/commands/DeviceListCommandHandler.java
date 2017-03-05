package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.data.view.UserDeviceView;
import de.njsm.stocks.client.config.Configuration;

public class DeviceListCommandHandler extends CommandHandler {

    public DeviceListCommandHandler(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List all the devices";
    }

    @Override
    public void handle(Command command) {
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
