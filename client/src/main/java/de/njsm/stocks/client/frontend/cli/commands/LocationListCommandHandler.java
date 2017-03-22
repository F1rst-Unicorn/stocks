package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.common.data.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LocationListCommandHandler extends CommandHandler {

    private static final Logger LOG = LogManager.getLogger(LocationListCommandHandler.class);

    public LocationListCommandHandler(Configuration c) {
        this.c = c;
        this.command = "list";
        this.description = "List the available food locations";
    }

    @Override
    public void handle(Command command) {
        listLocations();
    }

    public void listLocations() {
        try {
            List<Location> l = c.getDatabaseManager().getLocations();
            if (l.size() != 0) {
                System.out.println("Current locations: ");

                for (Location loc : l) {
                    System.out.println("\t" + loc.id + ": " + loc.name);
                }
            } else {
                System.out.println("\tNo locations there...");
            }
        } catch (DatabaseException e) {
            LOG.error("Error listing locations", e);
            System.out.println("Error fetching locations");
        }
    }
}
