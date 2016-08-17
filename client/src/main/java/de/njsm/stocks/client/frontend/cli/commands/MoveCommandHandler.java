package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.data.Food;
import de.njsm.stocks.client.data.FoodItem;
import de.njsm.stocks.client.data.Location;
import de.njsm.stocks.client.exceptions.SelectException;

import java.text.SimpleDateFormat;

public class MoveCommandHandler extends CommandHandler {

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
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Location[] locations;
        Food[] foods;
        int locId = -1;
        int foodId = -1;

        if (location.equals("")) {
            locations = c.getDatabaseManager().getLocations();
        } else {
            locations = c.getDatabaseManager().getLocations(location);
        }

        try {
            locId = LocationCommandHandler.selectLocation(locations, location);
        } catch (SelectException e) {
            e.printStackTrace();
        }

        if (food.equals("")) {
            foods = c.getDatabaseManager().getFood();
        } else {
            foods = c.getDatabaseManager().getFood(food);
        }

        try {
            foodId = FoodCommandHandler.selectFood(foods, food);
        } catch (SelectException e) {
            e.printStackTrace();
        }

        FoodItem[] items = c.getDatabaseManager().getItems(foodId);

        if (items.length == 0) {
            System.out.println("There is nothing to move here...");
            return;
        }

        System.out.println("Please choose an item");
        for (FoodItem i : items) {
            System.out.println(i.id + ": " + format.format(i.eatByDate));
        }

        int itemId = c.getReader().nextInt("Which one?", items[0].id);
        int index = 0;
        for (FoodItem i : items) {
            if (i.id == itemId) {
                break;
            }
            index++;
        }

        items[index].eatByDate = null;
        c.getServerManager().move(items[index], locId);

    }
}
