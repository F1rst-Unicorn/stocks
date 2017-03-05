package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.Location;

public class LocationRemoveCommandHandler extends CommandHandler {

    public LocationRemoveCommandHandler(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a location from the system";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            removeLocation(command.next());
        } else {
            removeLocation();
        }
    }

    public void removeLocation() {
        String name = c.getReader().next("Remove a location\nName: ");
        removeLocation(name);
    }

    public void removeLocation(String name) {
        try {
            Location[] l = c.getDatabaseManager().getLocations(name);
            int id = LocationCommandHandler.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id) {
                    c.getServerManager().removeLocation(loc);
                    (new RefreshCommandHandler(c, false)).refresh();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
