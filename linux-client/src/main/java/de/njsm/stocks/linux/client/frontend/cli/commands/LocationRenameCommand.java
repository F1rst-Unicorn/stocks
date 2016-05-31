package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class LocationRenameCommand extends Command {

    public LocationRenameCommand (Configuration c) {
        this.c = c;
        this.command = "rename";
        this.description = "Rename a location";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 2) {
            renameLocation(commands.get(0), commands.get(1));
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
            int id = LocationCommand.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id){
                    c.getServerManager().renameLocation(loc, newName);
                    (new RefreshCommand(c)).refreshLocations();
                }
            }

        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }

    }
}
