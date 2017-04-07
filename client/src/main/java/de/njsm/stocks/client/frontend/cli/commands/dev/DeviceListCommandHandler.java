package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.view.UserDeviceView;

import java.util.List;

public class DeviceListCommandHandler extends AbstractCommandHandler {

    private DatabaseManager dbManager;

    public DeviceListCommandHandler(ScreenWriter writer,
                                    DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List all the devices";
        this.dbManager = dbManager;
    }

    @Override
    public void handle(Command command) {
        try {
            List<UserDeviceView> devices = dbManager.getDevices();
            writer.printUserDeviceViews("Current devices: ", devices);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}
