package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class LocationRemoveCommandHandler extends AbstractCommandHandler {

    private Selector selector;

    public LocationRemoveCommandHandler(Configuration c, ScreenWriter writer, Selector selector) {
        super(writer);
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
            int id = selector.selectLocation(l, name).id;

            for (Location loc : l) {
                if (loc.id == id) {
                    c.getServerManager().removeLocation(loc);
                    (new RefreshCommandHandler(c, writer, false)).refresh();
                }
            }
        } catch (InputException |
                DatabaseException |
                NetworkException e) {
            System.out.println(e.getMessage());
        }
    }
}
