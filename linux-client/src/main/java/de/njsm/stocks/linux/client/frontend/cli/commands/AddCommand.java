package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCommand extends Command {

    public AddCommand(Configuration c) {
        command = "add";
        description = "Add a food item";
        this.c = c;

    }

    @Override
    public void handle(List<String> commands) {
        if (! commands.isEmpty() &&
            commands.get(0).equals("help")) {
            printHelp();
        } else if (commands.size() == 1) {
            addFood(commands.get(0));
        } else {
            addFood();
        }
    }

    public void addFood() {
        String type = c.getReader().next("What to add?  ");
        addFood(type);
    }


    public void addFood(String type) {
        try {
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommand.selectFood(foods, type);
            int locId = selectLocation(foodId);

            Date date = c.getReader().nextDate("Eat before:  ");

            FoodItem item = new FoodItem();
            item.ofType = foodId;
            item.storedIn = locId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();
            item.eatByDate = date;

            c.getServerManager().addItem(item);
            (new RefreshCommand(c)).refreshFoodItems();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

    protected int selectLocation(int foodId) throws SelectException {
        Location[] l = c.getDatabaseManager().getLocationsForFoodType(foodId);

        if (l.length == 0) {
            return selectLocation();
        } else {
            int result;
            System.out.println("Some food already in:");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            result = c.getReader().nextInt("Choose one or type -1 for new location", l[0].id);

            if (result == -1) {
                return selectLocation();
            } else {
                return result;
            }
        }
    }

    protected int selectLocation() throws SelectException {
        String location = c.getReader().next("Where is it stored?  ");
        Location[] locs = c.getDatabaseManager().getLocations(location);
        return LocationCommand.selectLocation(locs, location);
    }

}
