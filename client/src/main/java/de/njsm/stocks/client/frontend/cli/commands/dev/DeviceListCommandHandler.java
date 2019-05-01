package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.List;

public class DeviceListCommandHandler extends FaultyCommandHandler {

    private DatabaseManager dbManager;

    public DeviceListCommandHandler(ScreenWriter writer,
                                    DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List all the devices";
        this.dbManager = dbManager;
    }

    @Override
    protected void handleInternally(Command command) throws DatabaseException, NetworkException, InputException{
        List<UserDeviceView> devices = dbManager.getDevices();
        writer.printUserDeviceViews("Current devices: ", devices);
    }
}
