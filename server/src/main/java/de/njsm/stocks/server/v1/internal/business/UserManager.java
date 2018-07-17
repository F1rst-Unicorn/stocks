package de.njsm.stocks.server.v1.internal.business;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserFactory;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserManager {

    private static final Logger LOG = LogManager.getLogger(UserManager.class);

    private DatabaseHandler databaseHandler;

    private AuthAdmin authAdmin;

    public UserManager(DatabaseHandler databaseHandler, AuthAdmin authAdmin) {
        this.databaseHandler = databaseHandler;
        this.authAdmin = authAdmin;
    }

    public void addUser(User userToAdd) {
        if (Principals.isNameValid(userToAdd.name)) {
            databaseHandler.add(userToAdd);
        } else {
            LOG.warn("Tried to add invalid user " + userToAdd);
        }
    }

    public Data[] getUsers() {
        return databaseHandler.get(UserFactory.f);
    }

    public void removeUser(User user) {
        List<Integer> deviceIds = databaseHandler.getDeviceIdsOfUser(user);
        deviceIds.forEach(authAdmin::revokeCertificate);
        databaseHandler.remove(user);
    }


}
