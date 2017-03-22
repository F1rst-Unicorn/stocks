package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class RefreshCommandHandler extends CommandHandler {

    private static final Logger LOG = LogManager.getLogger(RefreshCommandHandler.class);


    private boolean verbose;

    public RefreshCommandHandler(Configuration c) {
        this(c, true);
    }

    public RefreshCommandHandler(Configuration c, boolean verbose) {
        this.c = c;
        command = "refresh";
        description = "Refresh the stocks system from the server";
        this.verbose = verbose;
    }

    @Override
    public void handle(Command command) {
        String word = command.next();
        if (word.equals("help")) {
            printHelp();
        } else {
            boolean fullUpdate = command.hasArg('f');
            refresh(fullUpdate);
        }
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean resetUpdates) {
        try {
            ServerManager sm = c.getServerManager();
            DatabaseManager dm = c.getDatabaseManager();

            if (resetUpdates) {
                dm.resetUpdates();
            }

            Update[] serverUpdates = sm.getUpdates();
            List<Update> localUpdates = dm.getUpdates();

            boolean upToDate = true;

            for (Update u : serverUpdates) {
                Update localUpdate = getLocalUpdate(localUpdates, u.table);
                if (localUpdate != null &&
                        u.lastUpdate.after(localUpdate.lastUpdate)) {
                    refreshTable(u.table);
                    upToDate = false;
                }
            }

            if (upToDate) {
                println("Already up to date");
            } else {
                dm.writeUpdates(Arrays.asList(serverUpdates));
            }
        } catch (DatabaseException |
                NetworkException e) {
            LOG.error("Error during database interaction", e);
            println("Refreshing not possible");
        }
    }

    private Update getLocalUpdate(List<Update> localUpdates, String table) {
        for (Update u : localUpdates) {
            if (u.table.equals(table)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void printHelp() {
        String help = "Get the latest updates from the server\n" +
                "\t-f\t\t\tForce update from server";
        System.out.println(help);
    }

    public void refreshTable(String tableName) throws DatabaseException, NetworkException {
        switch (tableName) {
            case "User":
                println("Refreshing users");
                refreshUsers();
                break;
            case "User_device":
                println("Refreshing devices");
                refreshDevices();
                break;
            case "Food":
                println("Refreshing food");
                refreshFood();
                break;
            case "Food_item":
                println("Refreshing food items");
                refreshFoodItems();
                break;
            case "Location":
                println("Refreshing locations");
                refreshLocations();
                break;
            default:
                // TODO Log
                break;
        }
    }

    public void refreshUsers() throws DatabaseException, NetworkException {
        List<User> serverUsers = Arrays.asList(c.getServerManager().getUsers());
        c.getDatabaseManager().writeUsers(serverUsers);
    }

    public void refreshDevices() throws DatabaseException, NetworkException {
        List<UserDevice> serverDevices = Arrays.asList(c.getServerManager().getDevices());
        c.getDatabaseManager().writeDevices(serverDevices);
    }

    public void refreshLocations() throws DatabaseException, NetworkException {
        List<Location> serverLocations = Arrays.asList(c.getServerManager().getLocations());
        c.getDatabaseManager().writeLocations(serverLocations);
    }

    public void refreshFood() throws DatabaseException, NetworkException {
        List<Food> serverFood = Arrays.asList(c.getServerManager().getFood());
        c.getDatabaseManager().writeFood(serverFood);
    }

    public void refreshFoodItems() throws DatabaseException, NetworkException {
        List<FoodItem> serverItems = Arrays.asList(c.getServerManager().getFoodItems());
        c.getDatabaseManager().writeFoodItems(serverItems);
    }

    private void println(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

}
