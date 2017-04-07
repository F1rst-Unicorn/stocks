package de.njsm.stocks.client.frontend.cli.commands.move;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    public InputCollector(DatabaseManager dbManager,
                          ScreenWriter writer,
                          InputReader reader) {
        super(writer, reader);
        this.dbManager = dbManager;
    }

    FoodItem createItem(Command command) throws DatabaseException, InputException {
        String foodFromUser = getInputFood(command);
        Food foodToMove = getFoodToMove(foodFromUser);
        return getItemToMove(foodToMove);
    }

    int createLocationId(Command command) throws DatabaseException, InputException {
        String locationFromUser = getInputLocation(command);
        return resolveLocation(locationFromUser).id;
    }

    private String getInputFood(Command command) throws DatabaseException {
        String result = "";
        if (command.hasArg('f')) {
            result = command.getParam('f');
        }

        if (result.isEmpty()) {
            result = askForFood();
        }
        return result;
    }

    private String askForFood() throws DatabaseException {
        listFood();
        return reader.next("What to move? ");
    }

    private Food getFoodToMove(String food) throws DatabaseException, InputException {
        List<Food> foodList = dbManager.getFood(food);
        return selectFood(foodList, food);
    }

    private FoodItem getItemToMove(Food food) throws DatabaseException, InputException {
        List<FoodItem> items = listItemsOfType(food);
        return selectItem(items);
    }

    private String getInputLocation(Command c) throws DatabaseException {
        String result = "";
        if (c.hasArg('l')) {
            result = c.getParam('l');
        }

        if (result.isEmpty()) {
            result = askForLocation();
        }
        return result;
    }

    private Location resolveLocation(String locationFromUser) throws DatabaseException, InputException {
        List<Location> locations = dbManager.getLocations(locationFromUser);
        return selectLocation(locations, locationFromUser);
    }

    private String askForLocation() throws DatabaseException {
        listLocations();
        return reader.next("Where to move to? ");
    }

    private void listFood() throws DatabaseException {
        List<Food> foodList = dbManager.getFood();
        writer.printFood("Available food: ", foodList);
    }

    private void listLocations() throws DatabaseException {
        List<Location> locationList = dbManager.getLocations();
        writer.printLocations("Available locations: ", locationList);
    }

    private List<FoodItem> listItemsOfType(Food food) throws DatabaseException {
        List<FoodItem> items = dbManager.getItems(food.id);
        writer.printItems("Items of this food type: ", items);
        return items;
    }
}
