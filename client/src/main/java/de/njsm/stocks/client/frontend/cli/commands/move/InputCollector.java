package de.njsm.stocks.client.frontend.cli.commands.move;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.commands.FoodCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.LocationCommandHandler;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

import java.util.List;

public class InputCollector {

    private DatabaseManager dbManager;

    private ScreenWriter writer;

    private InputReader reader;

    private Selector selector;

    public InputCollector(DatabaseManager dbManager,
                          Selector selector,
                          ScreenWriter writer,
                          InputReader reader) {
        this.dbManager = dbManager;
        this.selector = selector;
        this.writer = writer;
        this.reader = reader;
    }

    FoodItem createItem(Command command) throws DatabaseException, InputException {
        String foodFromUser = getInputFood(command);
        int foodToMove = getFoodToMove(foodFromUser);
        return getItemToMove(foodToMove);
    }

    int createLocationId(Command command) throws DatabaseException, InputException {
        String locationFromUser = getInputLocation(command);
        return resolveLocation(locationFromUser);
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

    private int getFoodToMove(String food) throws DatabaseException, InputException {
        List<Food> foodList = dbManager.getFood(food);
        return FoodCommandHandler.selectFood(foodList, food);
    }

    private FoodItem getItemToMove(int foodId) throws DatabaseException, InputException {
        List<FoodItem> items = listItemsOfType(foodId);
        return selector.selectItem(items);
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

    private int resolveLocation(String locationFromUser) throws DatabaseException, InputException {
        List<Location> locations = dbManager.getLocations(locationFromUser);
        return LocationCommandHandler.selectLocation(locations, locationFromUser);
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

    private List<FoodItem> listItemsOfType(int foodId) throws DatabaseException {
        List<FoodItem> items = dbManager.getItems(foodId);
        writer.printItems("Items of this food type: ", items);
        return items;
    }
}
