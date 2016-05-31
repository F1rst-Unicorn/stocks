package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Location;

import java.util.List;

public class LocationAddCommandHandler extends CommandHandler {

    public LocationAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new food location to the system";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            addLocation(commands.get(0));
        } else {
            addLocation();
        }
    }

    public void addLocation() {
        String name = c.getReader().nextName("Creating a new location\nName: ");
        addLocation(name);
    }

    public void addLocation(String name) {
        Location l = new Location();
        l.name = name;

        c.getServerManager().addLocation(l);

        (new RefreshCommandHandler(c)).refreshLocations();
    }
}
