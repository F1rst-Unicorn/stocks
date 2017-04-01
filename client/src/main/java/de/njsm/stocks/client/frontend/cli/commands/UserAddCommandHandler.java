package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.Refresher;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.common.data.User;

public class UserAddCommandHandler extends AbstractCommandHandler {

    private Refresher refresher;

    public UserAddCommandHandler(Configuration c, ScreenWriter writer, Refresher refresher) {
        super(writer);
        this.c = c;
        this.command = "add";
        this.description = "Add a new user";
        this.refresher = refresher;
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            addUser(command.next());
        } else {
            addUser();
        }
    }

    public void addUser() {
        String name = c.getReader().nextName("Creating a new user\nName: ");
        addUser(name);
    }

    public void addUser(String name) {
        try {
            User u = new User();
            u.name = name;

            if (!InputReader.isNameValid(name)) {
                addUser();
                return;
            }

            c.getServerManager().addUser(u);

            refresher.refresh();
        } catch (NetworkException e) {
            // TODO LOG
        } catch (DatabaseException e) {
            // TODO LOG
        }
    }
}
