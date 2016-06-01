package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.UserDevice;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;
import de.njsm.stocks.linux.client.exceptions.SelectException;

import java.util.List;

public class DeviceRemoveCommandHandler extends CommandHandler {

    public DeviceRemoveCommandHandler(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a device";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            removeDevice(command.next());
        } else {
            removeDevice();
        }
    }

    public void removeDevice() {
        String name = c.getReader().next("Remove a device\nName: ");
        removeDevice(name);
    }

    public void removeDevice(String name) {
        try {
            UserDeviceView[] devices = c.getDatabaseManager().getDevices(name);
            int id = DeviceCommandHandler.selectDevice(devices, name);

            for (UserDeviceView d : devices) {
                if (d.id == id) {
                    UserDevice rawDevice = new UserDevice();
                    rawDevice.id = d.id;
                    rawDevice.name = d.name;
                    c.getServerManager().removeDevice(rawDevice);
                    (new RefreshCommandHandler(c)).refreshDevices();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
