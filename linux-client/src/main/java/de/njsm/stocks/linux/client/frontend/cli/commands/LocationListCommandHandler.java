package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;

import java.util.List;

public class LocationListCommandHandler extends CommandHandler {

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
        Location[] l = c.getDatabaseManager().getLocations();
        if (l.length != 0) {
            System.out.println("Current locations: ");

            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
        } else {
            System.out.println("\tNo locations there...");
        }
    }
}
