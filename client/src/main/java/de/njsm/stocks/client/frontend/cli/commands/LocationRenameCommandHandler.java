package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class LocationRenameCommandHandler extends AbstractCommandHandler {

    private Selector selector;

    public LocationRenameCommandHandler(Configuration c, ScreenWriter writer, Selector selector) {
        super(writer);
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
            int id = selector.selectLocation(l, name).id;

            for (Location loc : l) {
                if (loc.id == id){
                    c.getServerManager().renameLocation(loc, newName);
                    (new RefreshCommandHandler(c, writer, false)).refresh();
                }
            }

        } catch (InputException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException |
                NetworkException e) {
            e.printStackTrace();
        }

    }
}
