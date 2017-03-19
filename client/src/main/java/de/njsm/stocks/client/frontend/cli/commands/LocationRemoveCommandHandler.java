package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.storage.DatabaseException;
import de.njsm.stocks.common.data.Location;

import java.util.List;

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
            List<Location> l = c.getDatabaseManager().getLocations(name);
            int id = LocationCommandHandler.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id) {
                    c.getServerManager().removeLocation(loc);
                    (new RefreshCommandHandler(c, false)).refresh();
                }
            }
        } catch (SelectException |
                DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }
}
