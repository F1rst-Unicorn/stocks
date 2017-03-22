package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.List;

public class MoveCommandHandler extends CommandHandler {

    private static final Logger LOG = LogManager.getLogger(MoveCommandHandler.class);

    protected String location;
    protected String food;

    public MoveCommandHandler(Configuration c) {
        command = "move";
        description = "Move a food item to a different location";
        this.c = c;
    }

    @Override
    public void handle(Command command) {
        String word = command.next();

        if (command.hasArg('l')) {
            location = command.getParam('l');
        } else {
            location = "";
        }

        if (command.hasArg('f')) {
            food = command.getParam('f');
        } else {
            food = "";
        }

        if (word.equals("help")) {
            printHelp();
        } else {
            moveFood();
        }
    }

    @Override
    public void printHelp() {
        String text = "Move a food item to a different location\n" +
                "\t--f string\t\t\tfood: The food type to move\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n\n";

        System.out.print(text);

    }

    protected void moveFood() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            List<Location> locations;
            List<Food> foods;
            int locId = -1;
            int foodId = -1;

            if (location.equals("")) {
                locations = c.getDatabaseManager().getLocations();
            } else {
                locations = c.getDatabaseManager().getLocations(location);
            }
            if (food.equals("")) {
                foods = c.getDatabaseManager().getFood();
            } else {
                foods = c.getDatabaseManager().getFood(food);
            }

            try {
                locId = LocationCommandHandler.selectLocation(locations, location);
                foodId = FoodCommandHandler.selectFood(foods, food);
            } catch (SelectException e) {
                e.printStackTrace();
                return;
            }

            List<FoodItem> items = c.getDatabaseManager().getItems(foodId);

            if (items.size() == 0) {
                System.out.println("There is nothing to move here...");
                return;
            }

            System.out.println("Please choose an item");
            for (FoodItem i : items) {
                System.out.println(i.id + ": " + format.format(i.eatByDate));
            }

            int itemId = c.getReader().nextInt("Which one?", items.get(0).id);
            int index = 0;
            for (FoodItem i : items) {
                if (i.id == itemId) {
                    break;
                }
                index++;
            }

            items.get(index).eatByDate = null;
            c.getServerManager().move(items.get(index), locId);
        } catch (DatabaseException |
                NetworkException e) {
            LOG.error("Could not move food", e);
            System.out.println("Could not move food");
        }
    }
}
