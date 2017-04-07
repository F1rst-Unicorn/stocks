package de.njsm.stocks.client.frontend.cli.commands.dev;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;

import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    public InputCollector(InputReader reader,
                          DatabaseManager dbManager,
                          ScreenWriter writer) {
        super(writer, reader);
        this.dbManager = dbManager;
    }

    User determineUser(Command c) throws DatabaseException, InputException {
        writer.println("Who is the owner?");
        String ownerName = resolveName(c, "User name: ");
        return resolveUser(ownerName);
    }

    UserDevice determineNewDevice(Command command, User owner) throws DatabaseException, InputException {
        writer.println("Adding a new device");
        UserDevice result = new UserDevice();
        result.name = resolveName(command, "Device name: ");
        result.userId = owner.id;
        return result;
    }

    boolean confirm() {
        return reader.getYesNo();
    }

    UserDevice determineDevice(Command c) throws DatabaseException, InputException {
        String name = resolveName(c, "Device name: ");
        return resolveInputName(name);
    }

    private String resolveName(Command c, String prompt) {
        if (c.hasNext()) {
            String inputName = c.next();
            if (InputReader.isNameValid(inputName)) {
                return c.next();
            } else {
                writer.println("Name may not contain '=' or '$'");
            }
        }
        return reader.nextName(prompt);
    }

    private UserDevice resolveInputName(String name) throws DatabaseException, InputException {
        List<UserDeviceView> devices = dbManager.getDevices(name);
        UserDeviceView view = selectDevice(devices, name);
        return new UserDevice(view.id, view.name, 0);
    }

    private User resolveUser(String name) throws InputException, DatabaseException {
        List<User> users = dbManager.getUsers(name);
        return selectUser(users, name);
    }
}
