package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.exceptions.ParseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.frontend.cli.service.Selector;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import java.util.List;

public class InputCollector extends Selector {

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    public InputCollector(ScreenWriter writer,
                          InputReader reader,
                          DatabaseManager dbManager,
                          TimeProvider timeProvider) {
        super(writer, reader);
        this.dbManager = dbManager;
        this.timeProvider = timeProvider;
    }

    public FoodItem createFoodItem(Command c) throws DatabaseException, InputException {
        FoodItem result = new FoodItem();
        result.ofType = determineFoodFromParameter(c).id;
        result.storedIn = determineLocationFromParameter(c, result.ofType).id;
        result.eatByDate = Instant.from(resolveDate(c).atStartOfDay(ZoneId.systemDefault()));
        return result;
    }

    public FoodItem determineNextItem(Command c) throws InputException, DatabaseException {
        String name = getFoodFromArgument(c);

        List<Food> foods = dbManager.getFood(name);
        Food food = selectFood(foods, name);
        return dbManager.getNextItem(food.id);
    }

    public FoodItem determineItem(Command command) throws DatabaseException, InputException {
        String foodFromUser = getFoodFromParameter(command);
        Food foodToMove = resolveFood(foodFromUser);
        return getItemToMove(foodToMove);
    }

    public User createUser(Command c) {
        User result = new User();
        result.name = resolveName(c, "User name: ");
        return result;
    }

    public User determineUser(Command c) throws DatabaseException, InputException {
        String ownerName = resolveName(c, "User name: ");
        List<User> users = dbManager.getUsers(ownerName);
        return selectUser(users, ownerName);
    }

    public UserDevice createDevice(Command command, User owner) throws DatabaseException, InputException {
        writer.println("Adding a new device");
        UserDevice result = new UserDevice();
        result.name = resolveName(command, "Device name: ");
        result.userId = owner.id;
        return result;
    }

    public UserDevice determineDevice(Command c) throws DatabaseException, InputException {
        String name = resolveName(c, "Device name: ");
        return resolveDevice(name);
    }

    public Food createFood(Command c) {
        Food result = new Food();
        result.name = determineNameFromCommandOrAsk("New food's name: ", c);
        return result;
    }

    public Food determineFood(Command c) throws DatabaseException, InputException {
        String name = determineNameFromCommandOrAsk("Food name: ", c);
        return resolveFood(name);
    }

    public Location createLocation(Command c) {
        Location result = new Location();
        result.name = determineNameFromCommandOrAsk("Location name: ", c);
        return result;
    }

    public Location determineLocation(Command c) throws DatabaseException, InputException {
        String name = determineNameFromCommandOrAsk("Location name: ", c);
        return resolveLocation(name);
    }

    public String determineNameFromCommandOrAsk(String prompt, Command c) {
        if (c.hasNext()) {
            return c.next();
        } else {
            return reader.next(prompt);
        }
    }

    public Location determineDestinationLocation(Command command) throws DatabaseException, InputException {
        String locationFromUser = getLocationFromParameter(command);
        return resolveLocation(locationFromUser);
    }

    public boolean confirm() {
        return reader.getYesNo();
    }

    private String resolveName(Command c, String prompt) {
        if (c.hasNext()) {
            String inputName = c.next();
            if (InputReader.isNameValid(inputName)) {
                return inputName;
            } else {
                writer.println("Name may not contain '=' or '$'");
            }
        }
        return reader.nextName(prompt);
    }

    private String getFoodFromArgument(Command c) {
        if (c.hasNext()) {
            return c.next();
        } else {
            return reader.next("What to eat?  ");
        }
    }

    private String getFoodFromParameter(Command command) throws DatabaseException {
        String result = "";
        if (command.hasArg('f')) {
            result = command.getParam('f');
        }

        if (result.isEmpty()) {
            result = askForFood();
        }
        return result;
    }

    private LocalDate resolveDate(Command command) {
        try {
            return resolveDateInternally(command);
        } catch (ParseException e) {
            return askForDate();
        }
    }

    private LocalDate resolveDateInternally(Command command) throws ParseException {
        if (command.hasArg('d')) {
            return command.getParamDate('d', timeProvider);
        } else {
            return askForDate();
        }
    }

    private LocalDate askForDate() {
        return reader.nextDate("Eat before:  ");
    }

    private Food determineFoodFromParameter(Command c) throws DatabaseException, InputException {
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

    private Location determineLocationFromParameter(Command c, int foodId) throws DatabaseException, InputException {
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
        return resolveLocation(location);
    }

    private Location getLocationFromUser() throws InputException, DatabaseException {
        String location = reader.next("Where is it stored?  ");
        return resolveLocation(location);
    }

    private FoodItem getItemToMove(Food food) throws DatabaseException, InputException {
        List<FoodItem> items = dbManager.getItems(food.id);
        return selectItem(items);
    }

    private String getLocationFromParameter(Command c) throws DatabaseException {
        String result = "";
        if (c.hasArg('l')) {
            result = c.getParam('l');
        }

        if (result.isEmpty()) {
            result = askForLocation();
        }
        return result;
    }

    private UserDevice resolveDevice(String name) throws DatabaseException, InputException {
        List<UserDeviceView> devices = dbManager.getDevices(name);
        UserDeviceView view = selectDevice(devices, name);
        return new UserDevice(view.id, view.name, view.userId);
    }

    private Food resolveFood(String food) throws DatabaseException, InputException {
        List<Food> foodList = dbManager.getFood(food);
        return selectFood(foodList, food);
    }

    private Location resolveLocation(String locationFromUser) throws DatabaseException, InputException {
        List<Location> locations = dbManager.getLocations(locationFromUser);
        return selectLocation(locations, locationFromUser);
    }

    private String askForFood() throws DatabaseException {
        listFood();
        return reader.next("What to move? ");
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
}
