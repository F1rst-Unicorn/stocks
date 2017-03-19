package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.storage.DatabaseException;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.client.exceptions.SelectException;

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
            List<Location> l = c.getDatabaseManager().getLocations(name);
            int id = LocationCommandHandler.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id){
                    c.getServerManager().renameLocation(loc, newName);
                    (new RefreshCommandHandler(c, false)).refresh();
                }
            }

        } catch (SelectException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

    }
}
