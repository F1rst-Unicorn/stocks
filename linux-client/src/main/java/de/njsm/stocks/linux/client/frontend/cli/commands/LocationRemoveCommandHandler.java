package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;

import java.util.List;

public class LocationRemoveCommandHandler extends CommandHandler {

    public LocationRemoveCommandHandler(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a location from the system";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            removeLocation(commands.get(0));
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
                    (new RefreshCommandHandler(c)).refreshLocations();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
