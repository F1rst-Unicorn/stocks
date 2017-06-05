package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.FaultyCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class LocationListCommandHandler extends FaultyCommandHandler {

    private DatabaseManager dbManager;

    public LocationListCommandHandler(ScreenWriter writer, DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List the available food locations";
        this.dbManager = dbManager;
    }

    @Override
    protected void handleInternally(Command command) throws NetworkException, DatabaseException, InputException {
        List<Location> l = dbManager.getLocations();
        writer.printLocations("Current locations: ", l);
    }
}
