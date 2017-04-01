package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class ScreenWriter {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private PrintStream outputStream;

    public ScreenWriter(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void println(String text) {
        outputStream.println(text);
    }

    public void printFood(String headline, List<Food> foodList) {
        if (foodList.isEmpty()) {
            println("\tNo food there...");
        } else {
            println(headline);

            for (Food f : foodList) {
                printFood(f);
            }
        }
    }

    public void printFood(Food f) {
        println("\t" + f.id + ": " + f.name);
    }

    public void printLocations(String headline, List<Location> locations) {
        if (locations.isEmpty()) {
            println("\tNo locations there...");
        } else {
            println(headline);

            for (Location loc : locations) {
                printLocation(loc);
            }
        }
    }

    public void printLocation(Location loc) {
        println("\t" + loc.id + ": " + loc.name);
    }

    public void printItems(String headline, List<FoodItem> items) {
        if (items.isEmpty()) {
            println("\tNo items there...");
        } else {
            println(headline);

            for (FoodItem i : items) {
                printItem(i);
            }
        }
    }

    public void printItem(FoodItem i) {
        println("\t\t" + i.id + ": " + format.format(i.eatByDate));
    }

    public void printUser(User input) {
        println("\t" + input.id + ": " + input.name);
    }

    public void printUsers(String headline, List<User> users) {
        if (users.isEmpty()) {
            println("No users there...");
        } else {
            println(headline);
            for (User u : users) {
                printUser(u);
            }
        }
    }
}
