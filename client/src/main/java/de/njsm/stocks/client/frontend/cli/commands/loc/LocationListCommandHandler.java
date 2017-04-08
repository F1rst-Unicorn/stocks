package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.commands.AbstractCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class LocationListCommandHandler extends AbstractCommandHandler {

    private DatabaseManager dbManager;

    public LocationListCommandHandler(ScreenWriter writer, DatabaseManager dbManager) {
        super(writer);
        this.command = "list";
        this.description = "List the available food locations";
        this.dbManager = dbManager;
    }

    @Override
    public void handle(Command command) {
        try {
            List<Location> l = dbManager.getLocations();
            writer.printLocations("Current locations: ", l);
        } catch (DatabaseException e) {
            logDatabaseError(e);
        }
    }
}
