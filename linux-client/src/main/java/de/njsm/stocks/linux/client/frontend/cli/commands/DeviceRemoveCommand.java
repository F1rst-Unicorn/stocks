package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.UserDevice;
import de.njsm.stocks.linux.client.data.view.UserDeviceView;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class DeviceRemoveCommand extends Command {

    public DeviceRemoveCommand(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a device";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            removeDevice(commands.get(0));
        } else {
            removeDevice();
        }
    }

    public void removeDevice() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove a device\nName: ");
        String name = scanner.next();
        removeDevice(name);
    }

    public void removeDevice(String name) {
        try {
            UserDeviceView[] devices = c.getDatabaseManager().getDevices(name);
            int id = DeviceCommand.selectDevice(devices, name);

            for (UserDeviceView d : devices) {
                if (d.id == id) {
                    UserDevice rawDevice = new UserDevice();
                    rawDevice.id = d.id;
                    rawDevice.name = d.name;
                    c.getServerManager().removeDevice(rawDevice);
                    (new RefreshCommand(c)).refreshDevices();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
