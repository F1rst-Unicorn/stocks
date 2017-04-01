package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;

import java.util.List;

public class DeviceRemoveCommandHandler extends AbstractCommandHandler {

    private Selector selector;

    private Refresher refresher;

    public DeviceRemoveCommandHandler(Configuration c, ScreenWriter writer, Selector selector, Refresher refresher) {
        super(writer);
        this.c = c;
        this.command = "remove";
        this.description = "Remove a device";
        this.refresher = refresher;
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
            List<UserDeviceView> devices = c.getDatabaseManager().getDevices(name);
            int id = selector.selectDevice(devices, name).id;

            for (UserDeviceView d : devices) {
                if (d.id == id) {
                    UserDevice rawDevice = new UserDevice();
                    rawDevice.id = d.id;
                    rawDevice.name = d.name;
                    c.getServerManager().removeDevice(rawDevice);
                    refresher.refresh();
                }
            }
        } catch (InputException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }
    }
}
