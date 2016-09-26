package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.data.*;
import de.njsm.stocks.client.network.server.ServerManager;
import de.njsm.stocks.client.storage.DatabaseManager;

import java.util.logging.Level;

public class RefreshCommandHandler extends CommandHandler {

    protected boolean verbose;

    public RefreshCommandHandler(Configuration c) {
        this.c = c;
        command = "refresh";
        description = "Refresh the stocks system from the server";
        verbose = true;
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
            if (command.hasArg('f')) {
                c.getDatabaseManager().resetUpdates();
            }
            refresh();
        }
    }

    public void refresh() {
        ServerManager sm = c.getServerManager();
        DatabaseManager dm = c.getDatabaseManager();

        Update[] serverUpdates = sm.getUpdates();
        Update[] localUpdates = dm.getUpdates();

        boolean upToDate = true;

        for (int i = 0; i < serverUpdates.length; i++){
            if (serverUpdates[i].lastUpdate.after(localUpdates[i].lastUpdate)) {
                refreshTable(serverUpdates[i].table);
                upToDate = false;
            }
        }

        if (upToDate) {
            println("Already up to date");
        } else {
            dm.writeUpdates(serverUpdates);
        }
    }

    @Override
    public void printHelp() {
        String help = "Get the latest updates from the server\n" +
                "\t-f\t\t\tForce update from server";
        System.out.println(help);
    }

    public void refreshTable(String tableName) {
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
                c.getLog().log(Level.WARNING, "Trying to refresh invalid table: " + tableName);
                break;
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

    public void refreshAll() {
        User[] serverUsers = c.getServerManager().getUsers();
        UserDevice[] serverDevices = c.getServerManager().getDevices();
        Location[] serverLocations = c.getServerManager().getLocations();
        Food[] serverFood = c.getServerManager().getFood();
        FoodItem[] serverItems = c.getServerManager().getFoodItems();
        c.getDatabaseManager().writeAll(serverUsers,
                serverDevices,
                serverLocations,
                serverFood,
                serverItems);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }



    private void println(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

}
