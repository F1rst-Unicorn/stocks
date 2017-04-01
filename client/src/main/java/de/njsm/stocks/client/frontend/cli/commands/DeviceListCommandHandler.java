package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DeviceListCommandHandler extends AbstractCommandHandler {

    private static final Logger LOG = LogManager.getLogger(DeviceListCommandHandler.class);


    public DeviceListCommandHandler(Configuration c, ScreenWriter writer) {
        super(writer);
        this.c = c;
        this.command = "list";
        this.description = "List all the devices";
    }

    @Override
    public void handle(Command command) {
        listDevices();
    }

    public void listDevices() {
        try {
            List<UserDeviceView> devices = c.getDatabaseManager().getDevices();
            System.out.println("Current devices: ");

            for (UserDeviceView dev : devices) {
                System.out.println("\t" + dev.id + ": " + dev.user + "'s " + dev.name);
            }
        } catch (DatabaseException e) {
            LOG.error("Could not list devices", e);
        }
    }

}
