package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.InputReader;
import de.njsm.stocks.client.frontend.cli.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.commands.FoodCommandHandler;
import de.njsm.stocks.client.frontend.cli.commands.LocationCommandHandler;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

import java.util.Date;
import java.util.List;

public class InputCollector {

    private DatabaseManager dbManager;

    private InputReader reader;

    private ScreenWriter writer;

    public InputCollector(DatabaseManager dbManager,
                          InputReader reader,
                          ScreenWriter writer) {
        this.dbManager = dbManager;
        this.reader = reader;
        this.writer = writer;
    }

    FoodItem createFoodItem(Command c) throws DatabaseException, InputException {
        FoodItem result = new FoodItem();
        result.ofType = resolveFood(c);
        result.storedIn = resolveLocation(c, result.ofType);
        result.eatByDate = resolveDate(c);
        return result;
    }

    private Date resolveDate(Command command) {
        try {
            return resolveDateInternally(command);
        } catch (ParseException e) {
            return askForDate();
        }
    }

    private Date resolveDateInternally(Command command) throws ParseException {
        if (command.hasArg('d')) {
            return command.getParamDate('d');
        } else {
            return askForDate();
        }
    }

    private Date askForDate() {
        return reader.nextDate("Eat before:  ");
    }

    private int resolveFood(Command c) throws DatabaseException, InputException {
        String type = getFoodName(c);
        List<Food> foods = dbManager.getFood(type);
        return FoodCommandHandler.selectFood(foods, type);
    }

    private String getFoodName(Command c) {
        String type;
        if (c.hasArg('f')) {
            type = c.getParam('f');
        } else {
            type = reader.next("What to add?  ");
        }
        return type;
    }

    private int resolveLocation(Command c, int foodId) throws DatabaseException, InputException {
        if (c.hasArg('l')) {
            return getLocationFromCommand(c);
        } else {
            return suggestLocationsBasedOnFoodType(foodId);
        }
    }

    private int suggestLocationsBasedOnFoodType(int foodId) throws DatabaseException, InputException {
        List<Location> l = dbManager.getLocationsForFoodType(foodId);

        if (l.isEmpty()) {
            return getLocationFromUser();
        } else {
            return getLocationFromExistingFood(l);
        }
    }

    private int getLocationFromExistingFood(List<Location> l) throws InputException, DatabaseException {
        listLocations(l);
        int result = reader.nextInt("Choose one or type -1 for new location", l.get(0).id);

        if (result == -1) {
            return getLocationFromUser();
        } else {
            return result;
        }
    }

    private void listLocations(List<Location> l) {
        writer.printLocations("Some food already in:", l);
    }

    private int getLocationFromCommand(Command c) throws DatabaseException, InputException {
        String location = c.getParam('l');
        return getLocationByName(location);
    }

    private int getLocationFromUser() throws InputException, DatabaseException {
        String location = reader.next("Where is it stored?  ");
        return getLocationByName(location);
    }

    private int getLocationByName(String location) throws DatabaseException, InputException {
        List<Location> locations = dbManager.getLocations(location);
        return LocationCommandHandler.selectLocation(locations, location);
    }
}
