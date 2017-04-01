package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.common.data.Location;

public class LocationAddCommandHandler extends AbstractCommandHandler {

    private Refresher refresher;

    public LocationAddCommandHandler(Configuration c, ScreenWriter writer, Refresher refresher) {
        super(writer);
        this.c = c;
        this.command = "add";
        this.description = "Add a new food location to the system";
        this.refresher = refresher;
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

            refresher.refresh();
        } catch (NetworkException e) {
            // TODO LOG
        } catch (DatabaseException e) {
            // TODO LOG
        }
    }
}
