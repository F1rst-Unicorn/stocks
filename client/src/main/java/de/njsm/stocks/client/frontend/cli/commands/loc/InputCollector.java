package de.njsm.stocks.client.frontend.cli.commands.loc;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    public InputCollector(ScreenWriter writer, InputReader reader, DatabaseManager dbManager) {
        super(writer, reader);
        this.dbManager = dbManager;
    }

    public Location resolveNewLocation(Command c) {
        Location result = new Location();
        result.name = getName(c);
        return result;
    }

    public Location resolveLocation(Command c) throws DatabaseException, InputException {
        String name = getName(c);
        List<Location> locations = dbManager.getLocations(name);
        return selectLocation(locations, name);
    }

    private String getName(Command c) {
        return getName("Location name: ", c);
    }

    public String getName(String prompt, Command c) {
        if (c.hasNext()) {
            return c.next();
        } else {
            return reader.next(prompt);
        }
    }

}
