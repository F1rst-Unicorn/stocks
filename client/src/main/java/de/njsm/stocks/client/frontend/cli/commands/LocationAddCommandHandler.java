package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.client.config.Configuration;

public class LocationAddCommandHandler extends CommandHandler {

    public LocationAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new food location to the system";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            addLocation(command.next());
        } else {
            addLocation();
        }
    }

    public void addLocation() {
        String name = c.getReader().nextName("Creating a new location\nName: ");
        addLocation(name);
    }

    public void addLocation(String name) {
        try {
            Location l = new Location();
            l.name = name;

            c.getServerManager().addLocation(l);

            (new RefreshCommandHandler(c, false)).refresh();
        } catch (NetworkException e) {
            // TODO LOG
        }
    }
}
