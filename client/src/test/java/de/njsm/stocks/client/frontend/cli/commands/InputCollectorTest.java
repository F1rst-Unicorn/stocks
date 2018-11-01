package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class InputCollectorTest {

    private InputCollector uut;

    private ScreenWriter writer;

    private InputReader reader;

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    @Before
    public void setup() throws Exception {
        writer = mock(ScreenWriter.class);
        reader = mock(InputReader.class);
        dbManager = mock(DatabaseManager.class);
        timeProvider = mock(TimeProvider.class);
        uut = new InputCollector(writer, reader, dbManager, timeProvider);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(writer);
        verifyNoMoreInteractions(reader);
        verifyNoMoreInteractions(dbManager);
        verifyNoMoreInteractions(timeProvider);
    }

    @Test
    public void testConfirmation() throws Exception {
        when(reader.getYesNo()).thenReturn(true);

        assertTrue(uut.confirm());
        verify(reader).getYesNo();
    }

    @Test
    public void createUserNonInteractivelyWorks() throws Exception {
        String name = "Jack";
        Command c = Command.createCommand(name);

        User output = uut.createUser(c);

        assertEquals(0, output.id);
        assertEquals(name, output.name);
    }

    @Test
    public void invalidNameAsksInteractively() throws Exception {
        String invalidName = "Jack$0";
        String interactiveName = "Jack";
        String prompt = "User name: ";
        Command c = Command.createCommand(invalidName);
        when(reader.nextName(prompt)).thenReturn(interactiveName);

        User output = uut.createUser(c);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        verify(reader).nextName(prompt);
        verify(writer).println("Name may not contain '=' or '$'");
    }

    @Test
    public void missingNameInCommandGoesInteractive() throws Exception {
        String interactiveName = "Jack";
        String prompt = "User name: ";
        Command c = Command.createCommand(new String[0]);
        when(reader.nextName(prompt)).thenReturn(interactiveName);

        User output = uut.createUser(c);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        verify(reader).nextName(prompt);
    }

    @Test
    public void missingLocationNameGoesInteractive() throws Exception {
        String interactiveName = "Fridge";
        Command c = Command.createCommand(new String[0]);
        String prompt = "Location name: ";
        when(reader.next(prompt)).thenReturn(interactiveName);

        Location output = uut.createLocation(c);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        verify(reader).next(prompt);
    }

    @Test
    public void createLocationNonInteractivelyWorks() throws Exception {
        String name = "Fridge";
        Command c = Command.createCommand(name);

        Location output = uut.createLocation(c);

        assertEquals(0, output.id);
        assertEquals(name, output.name);
    }

    @Test
    public void missingFoodNameGoesInteractive() throws Exception {
        String interactiveName = "Beer";
        Command c = Command.createCommand(new String[0]);
        String prompt = "New food's name: ";
        when(reader.next(prompt)).thenReturn(interactiveName);

        Food output = uut.createFood(c);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        verify(reader).next(prompt);
    }

    @Test
    public void createFoodNonInteractivelyWorks() throws Exception {
        String name = "Beer";
        Command c = Command.createCommand(name);

        Location output = uut.createLocation(c);

        assertEquals(0, output.id);
        assertEquals(name, output.name);
    }

    @Test
    public void missingDeviceNameGoesInteractive() throws Exception {
        String interactiveName = "Mobile";
        User owner = new User(1, "Jack");
        String prompt = "Device name: ";
        Command c = Command.createCommand(new String[0]);
        when(reader.nextName(prompt)).thenReturn(interactiveName);

        UserDevice output = uut.createDevice(c, owner);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        assertEquals(owner.id, output.userId);
        verify(reader).nextName(prompt);
        verify(writer).println("Adding a new device");
    }

    @Test
    public void invalidNameGoesInteractive() throws Exception {
        String invalidName = "1$Mobile";
        String interactiveName = "Mobile";
        User owner = new User(1, "Jack");
        String prompt = "Device name: ";
        Command c = Command.createCommand(invalidName);
        when(reader.nextName(prompt)).thenReturn(interactiveName);

        UserDevice output = uut.createDevice(c, owner);

        assertEquals(0, output.id);
        assertEquals(interactiveName, output.name);
        assertEquals(owner.id, output.userId);
        verify(reader).nextName(prompt);
        verify(writer).println("Adding a new device");
        verify(writer).println("Name may not contain '=' or '$'");
    }

    @Test
    public void creatingDeviceNonInteractivelyWorks() throws Exception {
        String name = "Mobile";
        User owner = new User(1, "Jack");
        Command c = Command.createCommand(name);

        UserDevice output = uut.createDevice(c, owner);

        assertEquals(0, output.id);
        assertEquals(name, output.name);
        assertEquals(owner.id, output.userId);
        verify(writer).println("Adding a new device");
    }

    @Test
    public void createItemNonInteractivelyWorks() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        setupMockDatabase(food, location);
        String inputDate = "01.01.2015";
        LocalDate date = LocalDate.parse("2015-01-01");
        Command c = Command.createCommand(new String[] {
                "--f",
                food.name,
                "--l",
                location.name,
                "--d",
                inputDate
        });

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toInstant(), output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocations(location.name);
    }

    @Test
    public void createItemUsesCorrectTimezone() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        setupMockDatabase(food, location);
        String inputDate = "01.01.1970";
        Command c = Command.createCommand(new String[] {
                "--f",
                food.name,
                "--l",
                location.name,
                "--d",
                inputDate
        });

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(Instant.EPOCH, output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocations(location.name);
    }

    @Test
    public void createItemInvalidDateGoesInteractive() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        setupMockDatabase(food, location);
        String invalidDate = "fdsa";
        LocalDate date = LocalDate.parse("2015-01-01");
        String datePrompt = "Eat before:  ";
        Command c = Command.createCommand(new String[] {
                "--f",
                food.name,
                "--l",
                location.name,
                "--d",
                invalidDate
        });
        when(reader.nextDate(datePrompt)).thenReturn(date);

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toInstant(), output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocations(location.name);
        verify(reader).nextDate(datePrompt);
    }

    @Test
    public void createItemInteractively() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        LocalDate date = LocalDate.parse("2015-01-01");
        setupMockDatabase(food, location);
        String foodPrompt = "What to add?  ";
        String locationPrompt = "Choose one or type -1 for new location";
        String datePrompt = "Eat before:  ";
        Command c = Command.createCommand(new String[0]);
        when(reader.nextDate(datePrompt)).thenReturn(date);
        when(reader.next(foodPrompt)).thenReturn(food.name);
        when(reader.nextInt(locationPrompt, location.id)).thenReturn(location.id);

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toInstant(), output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocationsForFoodType(food.id);
        verify(reader).nextDate(datePrompt);
        verify(reader).next(foodPrompt);
        verify(reader).nextInt(locationPrompt, location.id);
        verify(writer).printLocations("Some food already in:",
                Collections.singletonList(location));
    }

    @Test
    public void createItemInvalidLocationSelection() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        LocalDate date = LocalDate.parse("2015-01-01");
        setupMockDatabase(food, location);
        String foodPrompt = "What to add?  ";
        String locationPrompt = "Choose one or type -1 for new location";
        String locationPrompt2 = "Where is it stored?  ";
        String datePrompt = "Eat before:  ";
        Command c = Command.createCommand(new String[0]);
        when(reader.nextDate(datePrompt)).thenReturn(date);
        when(reader.next(foodPrompt)).thenReturn(food.name);
        when(reader.nextInt(locationPrompt, location.id)).thenReturn(-1);
        when(reader.next(locationPrompt2)).thenReturn(location.name);

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toInstant(), output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocationsForFoodType(food.id);
        verify(dbManager).getLocations(location.name);
        verify(reader).nextDate(datePrompt);
        verify(reader).next(foodPrompt);
        verify(reader).nextInt(locationPrompt, location.id);
        verify(reader).next(locationPrompt2);
        verify(writer).printLocations("Some food already in:",
                Collections.singletonList(location));
    }

    @Test
    public void createInteractivelyWithoutSuggestion() throws Exception {
        Food food = new Food(4, "Beer");
        Location location = new Location(5, "Fridge");
        LocalDate date = LocalDate.parse("2015-01-01");
        setupMockDatabase(food, location);
        String foodPrompt = "What to add?  ";
        String locationPrompt = "Where is it stored?  ";
        String datePrompt = "Eat before:  ";
        Command c = Command.createCommand(new String[0]);
        when(dbManager.getLocationsForFoodType(anyInt())).thenReturn(Collections.emptyList());
        when(reader.nextDate(datePrompt)).thenReturn(date);
        when(reader.next(foodPrompt)).thenReturn(food.name);
        when(reader.next(locationPrompt)).thenReturn(location.name);

        FoodItem output = uut.createFoodItem(c);

        assertEquals(food.id, output.ofType);
        assertEquals(location.id, output.storedIn);
        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toInstant(), output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocationsForFoodType(food.id);
        verify(dbManager).getLocations(location.name);
        verify(reader).nextDate(datePrompt);
        verify(reader).next(foodPrompt);
        verify(reader).next(locationPrompt);
    }

    @Test
    public void getNextItemNonInteractively() throws Exception {
        Food input = new Food(2, "Beer");
        FoodItem expected = new FoodItem(2, Instant.now(), input.id, 0, 0, 0);
        Command command = Command.createCommand(input.name);
        setupMockDatabase(input, new Location());
        when(dbManager.getNextItem(input.id)).thenReturn(expected);

        FoodItem output = uut.determineNextItem(command);

        assertEquals(expected, output);
        verify(dbManager).getFood(input.name);
        verify(dbManager).getNextItem(input.id);
    }

    @Test
    public void getNextItemInteractively() throws Exception {
        Food input = new Food(2, "Beer");
        FoodItem expected = new FoodItem(2, Instant.now(), input.id, 0, 0, 0);
        Command command = Command.createCommand(new String[0]);
        String prompt = "What to eat?  ";
        setupMockDatabase(input, new Location());
        when(dbManager.getNextItem(input.id)).thenReturn(expected);
        when(reader.next(prompt)).thenReturn(input.name);

        FoodItem output = uut.determineNextItem(command);

        assertEquals(expected, output);
        verify(dbManager).getFood(input.name);
        verify(dbManager).getNextItem(input.id);
        verify(reader).next(prompt);
    }

    @Test
    public void determineDestinationNonInteractively() throws Exception {
        Location expected = new Location(2, "Fridge");
        Command command = Command.createCommand("--l " + expected.name);
        setupMockDatabase(new Food(), expected);

        Location output = uut.determineDestinationLocation(command);

        assertEquals(expected, output);
        verify(dbManager).getLocations(expected.name);
    }

    @Test
    public void determineDestinationInteractively() throws Exception {
        Location expected = new Location(2, "Fridge");
        Command command = Command.createCommand(new String[0]);
        String prompt = "Where to move to? ";
        setupMockDatabase(new Food(), expected);
        when(dbManager.getLocations()).thenReturn(Collections.singletonList(expected));
        when(dbManager.getLocations(expected.name)).thenReturn(Collections.singletonList(expected));
        when(reader.next(prompt)).thenReturn(expected.name);

        Location output = uut.determineDestinationLocation(command);

        assertEquals(expected, output);
        verify(dbManager).getLocations();
        verify(dbManager).getLocations(expected.name);
        verify(reader).next(prompt);
        verify(writer).printLocations("Available locations: ", Collections.singletonList(expected));
    }

    @Test
    public void determineLocationNonInteractively() throws Exception {
        Location expected = new Location(2, "Fridge");
        Command command = Command.createCommand(expected.name);
        setupMockDatabase(new Food(), expected);

        Location output = uut.determineLocation(command);

        assertEquals(expected, output);
        verify(dbManager).getLocations(expected.name);
    }

    @Test
    public void determineLocationInteractively() throws Exception {
        Location expected = new Location(2, "Fridge");
        Command command = Command.createCommand(new String[0]);
        String prompt = "Location name: ";
        setupMockDatabase(new Food(), expected);
        when(reader.next(prompt)).thenReturn(expected.name);

        Location output = uut.determineLocation(command);

        assertEquals(expected, output);
        verify(dbManager).getLocations(expected.name);
        verify(reader).next(prompt);
    }

    @Test
    public void determineFoodNonInteractively() throws Exception {
        Food expected = new Food(2, "Beer");
        Command command = Command.createCommand(expected.name);
        setupMockDatabase(expected, new Location());

        Food output = uut.determineFood(command);

        assertEquals(expected, output);
        verify(dbManager).getFood(expected.name);
    }

    @Test
    public void determineFoodInteractively() throws Exception {
        Food expected = new Food(2, "Beer");
        Command command = Command.createCommand(new String[0]);
        String prompt = "Food name: ";
        setupMockDatabase(expected, new Location());
        when(reader.next(prompt)).thenReturn(expected.name);

        Food output = uut.determineFood(command);

        assertEquals(expected, output);
        verify(dbManager).getFood(expected.name);
        verify(reader).next(prompt);
    }

    @Test
    public void determineDeviceNonInteractively() throws Exception {
        UserDevice expected = new UserDevice(2, "Mobile", 1);
        Command command = Command.createCommand(expected.name);
        when(dbManager.getDevices(expected.name)).thenReturn(
                Collections.singletonList(new UserDeviceView(
                        expected.id,
                        expected.name,
                        "John",
                        expected.userId
                )));

        UserDevice output = uut.determineDevice(command);

        assertEquals(expected, output);
        verify(dbManager).getDevices(expected.name);
    }

    @Test
    public void determineDeviceInteractively() throws Exception {
        UserDevice expected = new UserDevice(2, "Mobile", 1);
        Command command = Command.createCommand(new String[0]);
        String prompt = "Device name: ";
        when(dbManager.getDevices(expected.name)).thenReturn(
                Collections.singletonList(new UserDeviceView(
                        expected.id,
                        expected.name,
                        "John",
                        expected.userId
                )));
        when(reader.nextName(prompt)).thenReturn(expected.name);

        UserDevice output = uut.determineDevice(command);

        assertEquals(expected, output);
        verify(dbManager).getDevices(expected.name);
        verify(reader).nextName(prompt);
    }

    @Test
    public void determineDeviceInvalidNameGoesInteractively() throws Exception {
        UserDevice expected = new UserDevice(2, "Mobile", 1);
        Command command = Command.createCommand("$invalid$name$");
        String prompt = "Device name: ";
        when(dbManager.getDevices(expected.name)).thenReturn(
                Collections.singletonList(new UserDeviceView(
                        expected.id,
                        expected.name,
                        "John",
                        expected.userId
                )));
        when(reader.nextName(prompt)).thenReturn(expected.name);

        UserDevice output = uut.determineDevice(command);

        assertEquals(expected, output);
        verify(dbManager).getDevices(expected.name);
        verify(reader).nextName(prompt);
        verify(writer).println("Name may not contain '=' or '$'");
    }

    @Test
    public void determineUserNonInteractively() throws Exception {
        User expected = new User(2, "John");
        Command command = Command.createCommand(expected.name);
        when(dbManager.getUsers(expected.name)).thenReturn(Collections.singletonList(expected));

        User output = uut.determineUser(command);

        assertEquals(expected, output);
        verify(dbManager).getUsers(expected.name);
    }

    @Test
    public void determineUserInteractively() throws Exception {
        User expected = new User(2, "John");
        Command command = Command.createCommand(new String[0]);
        String prompt = "User name: ";
        when(dbManager.getUsers(expected.name)).thenReturn(
                Collections.singletonList(new User(expected.id,expected.name)));
        when(reader.nextName(prompt)).thenReturn(expected.name);

        User output = uut.determineUser(command);

        assertEquals(expected, output);
        verify(dbManager).getUsers(expected.name);
        verify(reader).nextName(prompt);
    }

    @Test
    public void determineUserInvalidNameGoesInteractively() throws Exception {
        User expected = new User(2, "John");
        Command command = Command.createCommand("$invalid$name=");
        String prompt = "User name: ";
        when(dbManager.getUsers(expected.name)).thenReturn(
                Collections.singletonList(new User(expected.id,expected.name)));
        when(reader.nextName(prompt)).thenReturn(expected.name);

        User output = uut.determineUser(command);

        assertEquals(expected, output);
        verify(dbManager).getUsers(expected.name);
        verify(reader).nextName(prompt);
        verify(writer).println("Name may not contain '=' or '$'");
    }

    @Test
    public void determineItemNonInteractively() throws Exception {
        Food expectedType = new Food(2, "Beer");
        FoodItem expected = new FoodItem(2, Instant.now(), expectedType.id, 2, 3, 4);
        Command command = Command.createCommand("--f " + expectedType.name);
        setupMockDatabase(expectedType, new Location());
        when(dbManager.getItems(expectedType.id)).thenReturn(Collections.singletonList(expected));

        FoodItem output = uut.determineItem(command);

        assertEquals(expectedType.id, output.ofType);
        verify(dbManager).getFood(expectedType.name);
        verify(dbManager).getItems(expectedType.id);
    }

    @Test
    public void determineItemInteractively() throws Exception {
        Food expectedType = new Food(2, "Beer");
        FoodItem expected = new FoodItem(2, Instant.now(), expectedType.id, 2, 3, 4);
        Command command = Command.createCommand(new String[0]);
        String prompt = "What to move? ";
        setupMockDatabase(expectedType, new Location());
        when(dbManager.getItems(expectedType.id)).thenReturn(Collections.singletonList(expected));
        when(dbManager.getFood()).thenReturn(Collections.singletonList(expectedType));
        when(reader.next(prompt)).thenReturn(expectedType.name);

        FoodItem output = uut.determineItem(command);

        assertEquals(expectedType.id, output.ofType);
        verify(dbManager).getFood(expectedType.name);
        verify(dbManager).getFood();
        verify(dbManager).getItems(expectedType.id);
        verify(reader).next(prompt);
        verify(writer).printFood("Available food: ", Collections.singletonList(expectedType));
    }

    private void setupMockDatabase(Food food, Location location) throws DatabaseException {
        when(dbManager.getFood(food.name)).thenReturn(
                Collections.singletonList(food));
        when(dbManager.getLocations(location.name)).thenReturn(
                Collections.singletonList(location));
        when(dbManager.getLocationsForFoodType(food.id)).thenReturn(
                Collections.singletonList(location));
    }
}