package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.*;
import de.njsm.stocks.linux.client.network.server.ServerManager;
import de.njsm.stocks.linux.client.storage.DatabaseManager;

import java.util.List;
import java.util.logging.Level;

public class RefreshCommand extends Command {

    public RefreshCommand(Configuration c) {
        this.c = c;
        command = "refresh";
    }

    @Override
    public void handle(List<String> commands) {
        ServerManager sm = c.getServerManager();
        DatabaseManager dm = c.getDatabaseManager();

        Update[] serverUpdates = sm.getUpdates();
        Update[] localUpdates = dm.getUpdates();

        for (int i = 0; i < serverUpdates.length; i++){
            if (serverUpdates[i].lastUpdate.after(localUpdates[i].lastUpdate)) {
                refreshTable(serverUpdates[i].table);
            }
        }

        dm.writeUpdates(serverUpdates);
    }

    public void refreshTable(String tableName) {
        if (tableName.equals("User")) {
            refreshUsers();
        } else if (tableName.equals("User_device")) {
            refreshDevices();
        } else if (tableName.equals("Food")) {
            refreshFood();
        } else if (tableName.equals("Food_item")) {
            refreshFoodItems();
        } else if (tableName.equals("Location")) {
            refreshLocations();
        } else {
            c.getLog().log(Level.WARNING, "Trying to refresh invalid tablename: " + tableName);
        }
    }

    public void refreshUsers() {
        User[] serverUsers = c.getServerManager().getUsers();
        c.getDatabaseManager().writeUsers(serverUsers);
    }

    public void refreshDevices() {
        UserDevice[] serverDevices = c.getServerManager().getDevices();
        c.getDatabaseManager().writeDevices(serverDevices);
    }

    public void refreshLocations() {
        Location[] serverLocations = c.getServerManager().getLocations();
        c.getDatabaseManager().writeLocations(serverLocations);
    }

    public void refreshFood() {
        Food[] serverFood = c.getServerManager().getFood();
        c.getDatabaseManager().writeFood(serverFood);
    }

    public void refreshFoodItems() {
        FoodItem[] serverItems = c.getServerManager().getFoodItems();
        c.getDatabaseManager().writeFoodItems(serverItems);
    }
}
