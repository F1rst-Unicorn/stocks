package de.njsm.stocks.client.frontend.cli.commands.add;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;

import java.util.Date;
import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    public InputCollector(DatabaseManager dbManager,
                          InputReader reader,
                          ScreenWriter writer,
                          TimeProvider timeProvider) {
        super(writer, reader);
        this.dbManager = dbManager;
        this.timeProvider = timeProvider;
    }

    FoodItem createFoodItem(Command c) throws DatabaseException, InputException {
        c.setTimeProvider(timeProvider);
        FoodItem result = new FoodItem();
        result.ofType = resolveFood(c).id;
        result.storedIn = resolveLocation(c, result.ofType).id;
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

    private Food resolveFood(Command c) throws DatabaseException, InputException {
        String type = getFoodName(c);
        List<Food> foods = dbManager.getFood(type);
        return selectFood(foods, type);
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

    private Location resolveLocation(Command c, int foodId) throws DatabaseException, InputException {
        if (c.hasArg('l')) {
            return getLocationFromCommand(c);
        } else {
            return suggestLocationsBasedOnFoodType(foodId);
        }
    }

    private Location suggestLocationsBasedOnFoodType(int foodId) throws DatabaseException, InputException {
        List<Location> l = dbManager.getLocationsForFoodType(foodId);

        if (l.isEmpty()) {
            return getLocationFromUser();
        } else {
            return getLocationFromExistingFood(l);
        }
    }

    private Location getLocationFromExistingFood(List<Location> l) throws InputException, DatabaseException {
        writer.printLocations("Some food already in:", l);
        int resultId = reader.nextInt("Choose one or type -1 for new location", l.get(0).id);
        Location result = l.stream().filter(i -> i.id == resultId).findFirst().orElse(null);

        if (result == null) {
            return getLocationFromUser();
        } else {
            return result;
        }
    }

    private Location getLocationFromCommand(Command c) throws DatabaseException, InputException {
        String location = c.getParam('l');
        return getLocationByName(location);
    }

    private Location getLocationFromUser() throws InputException, DatabaseException {
        String location = reader.next("Where is it stored?  ");
        return getLocationByName(location);
    }

    private Location getLocationByName(String location) throws DatabaseException, InputException {
        List<Location> locations = dbManager.getLocations(location);
        return selectLocation(locations, location);
    }
}
