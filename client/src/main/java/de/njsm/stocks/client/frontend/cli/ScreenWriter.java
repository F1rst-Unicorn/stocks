package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

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
            outputStream.println("\tNo food there...");
        } else {
            outputStream.println(headline);

            for (Food f : foodList) {
                printFood(f);
            }
        }
    }

    public void printFood(Food f) {
        outputStream.println("\t" + f.id + ": " + f.name);
    }

    public void printLocations(String headline, List<Location> locations) {
        if (locations.isEmpty()) {
            outputStream.println("\tNo locations there...");
        } else {
            outputStream.println(headline);

            for (Location loc : locations) {
                printLocation(loc);
            }
        }
    }

    public void printLocation(Location loc) {
        outputStream.println("\t" + loc.id + ": " + loc.name);
    }

    public void printItems(String headline, List<FoodItem> items) {
        if (items.isEmpty()) {
            outputStream.println("\tNo items to show...");
        } else {
            outputStream.println(headline);

            for (FoodItem i : items) {
                printItem(i);
            }
        }
    }

    public void printItem(FoodItem i) {
        outputStream.println("\t\t" + i.id + ": " + format.format(i.eatByDate));
    }
}
