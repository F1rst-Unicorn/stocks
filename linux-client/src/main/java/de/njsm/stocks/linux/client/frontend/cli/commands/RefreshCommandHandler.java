package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.*;
import de.njsm.stocks.linux.client.network.server.ServerManager;
import de.njsm.stocks.linux.client.storage.DatabaseManager;

import java.util.List;
import java.util.logging.Level;

public class RefreshCommandHandler extends CommandHandler {

    public RefreshCommandHandler(Configuration c) {
        this.c = c;
        command = "refresh";
        description = "Refresh the stocks system from the server";
    }

    @Override
    public void handle(Command command) {
        String word = command.next();
        if (word.equals("help")) {
            printHelp();
        } else {
            refresh();
        }
    }

    @Override
    public void handle(List<String> commands) {

        if (commands.size() == 2) {
            printHelp();
        } else {
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
            System.out.println("Already up to date");
        } else {
            dm.writeUpdates(serverUpdates);
        }
    }

    @Override
    public void printHelp() {
        String help = "refresh command\n" +
                "\n" +
                "\tGet the latest updates from the server";
        System.out.println(help);
    }

    public void refreshTable(String tableName) {
        switch (tableName) {
            case "User":
                System.out.println("Refreshing users");
                refreshUsers();
                break;
            case "User_device":
                System.out.println("Refreshing devices");
                refreshDevices();
                break;
            case "Food":
                System.out.println("Refreshing food");
                refreshFood();
                break;
            case "Food_item":
                System.out.println("Refreshing food items");
                refreshFoodItems();
                break;
            case "Location":
                System.out.println("Refreshing locations");
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

}
