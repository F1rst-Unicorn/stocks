package de.njsm.stocks.client.frontend.cli.commands.user;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.User;

import java.util.List;

public class InputCollector extends Selector {


    private final DatabaseManager dbManager;

    public InputCollector(ScreenWriter writer, InputReader reader, DatabaseManager dbManager) {
        super(writer, reader);
        this.dbManager = dbManager;
    }

    public User resolveUser(Command c) throws InputException, DatabaseException {
        String name;

        if (c.hasNext()) {
            name = c.next();
        } else {
            name = askForName();
        }

        List<User> users = dbManager.getUsers(name);
        return selectUser(users, name);
    }

    private String askForName() {
        return reader.nextName("User name: ");
    }
}
