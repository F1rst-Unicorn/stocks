package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class LocationRemoveCommand extends Command {

    public LocationRemoveCommand(Configuration c) {
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
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove a location\nName: ");
        String name = scanner.next();
        removeLocation(name);
    }

    public void removeLocation(String name) {
        try {
            Location[] l = c.getDatabaseManager().getLocations(name);
            int id = LocationCommand.selectLocation(l, name);

            for (Location loc : l) {
                if (loc.id == id) {
                    c.getServerManager().removeLocation(loc);
                    (new RefreshCommand(c)).refreshLocations();
                }
            }
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }
}
