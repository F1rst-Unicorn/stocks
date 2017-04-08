package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Refresher {

    private static final Logger LOG = LogManager.getLogger(Refresher.class);

    private ServerManager serverManager;

    private DatabaseManager dbManager;

    public Refresher(ServerManager serverManager, DatabaseManager dbManager) {
        this.serverManager = serverManager;
        this.dbManager = dbManager;
    }

    public boolean refresh() throws DatabaseException, NetworkException {
        return refresh(false);
    }

    public void refreshFull() throws DatabaseException, NetworkException {
        refresh(true);
    }

    private boolean refresh(boolean resetUpdates) throws DatabaseException, NetworkException {
        if (resetUpdates) {
            dbManager.resetUpdates();
        }

        Update[] serverUpdates = serverManager.getUpdates();
        List<Update> localUpdates = dbManager.getUpdates();

        boolean upToDate = true;

        for (Update u : serverUpdates) {
            Update localUpdate = getLocalUpdate(localUpdates, u.table);
            if (localUpdate != null &&
                    u.lastUpdate.after(localUpdate.lastUpdate)) {
                refreshTable(u.table);
                upToDate = false;
            }
        }

        dbManager.writeUpdates(Arrays.asList(serverUpdates));

        return upToDate;
    }

    private Update getLocalUpdate(List<Update> localUpdates, String table) {
        for (Update u : localUpdates) {
            if (u.table.equals(table)) {
                return u;
            }
        }
        return null;
    }

    private void refreshTable(String tableName) throws DatabaseException, NetworkException {
        switch (tableName) {
            case "User":
                refreshUsers();
                break;
            case "User_device":
                refreshDevices();
                break;
            case "Food":
                refreshFood();
                break;
            case "Food_item":
                refreshFoodItems();
                break;
            case "Location":
                refreshLocations();
                break;
            default:
                LOG.error("Trying to refresh invalid table " + tableName);
        }
    }

    private void refreshUsers() throws DatabaseException, NetworkException {
        List<User> serverUsers = Arrays.asList(serverManager.getUsers());
        dbManager.writeUsers(serverUsers);
    }

    private void refreshDevices() throws DatabaseException, NetworkException {
        List<UserDevice> serverDevices = Arrays.asList(serverManager.getDevices());
        dbManager.writeDevices(serverDevices);
    }

    private void refreshLocations() throws DatabaseException, NetworkException {
        List<Location> serverLocations = Arrays.asList(serverManager.getLocations());
        dbManager.writeLocations(serverLocations);
    }

    private void refreshFood() throws DatabaseException, NetworkException {
        List<Food> serverFood = Arrays.asList(serverManager.getFood());
        dbManager.writeFood(serverFood);
    }

    private void refreshFoodItems() throws DatabaseException, NetworkException {
        List<FoodItem> serverItems = Arrays.asList(serverManager.getFoodItems());
        dbManager.writeFoodItems(serverItems);
    }

}
