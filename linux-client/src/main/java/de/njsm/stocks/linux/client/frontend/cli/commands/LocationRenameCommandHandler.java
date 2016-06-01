package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;

import java.util.List;

public class LocationRenameCommandHandler extends CommandHandler {

    public LocationRenameCommandHandler(Configuration c) {
        this.c = c;
        this.command = "rename";
        this.description = "Rename a location";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            String name = command.next();
            if (command.hasNext()) {
                renameLocation(name, command.next());
            } else {
                renameLocation();
            }
        } else {
            renameLocation();
        }
    }

    public void renameLocation() {
        String name = c.getReader().next("Rename a location\nName: ");
        String newName = c.getReader().next("New name: ");
        renameLocation(name, newName);
    }

    public void renameLocation(String name, String newName) {
        try {
            Location[] l = c.getDatabaseManager().getLocations(name);
            int id = LocationCommandHandler.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id){
                    c.getServerManager().renameLocation(loc, newName);
                    (new RefreshCommandHandler(c)).refreshLocations();
                }
            }

        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }

    }
}
