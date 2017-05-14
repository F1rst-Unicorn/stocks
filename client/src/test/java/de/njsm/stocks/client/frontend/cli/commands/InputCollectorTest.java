package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.frontend.cli.Command;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.frontend.cli.service.ScreenWriter;
import de.njsm.stocks.client.service.TimeProvider;
import de.njsm.stocks.client.storage.DatabaseManager;
import de.njsm.stocks.common.data.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class InputCollectorTest {

    private InputCollector uut;

    private ScreenWriter writer;

    private InputReader reader;

    private DatabaseManager dbManager;

    private TimeProvider timeProvider;

    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

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
        Date date = format.parse(inputDate);
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
        assertEquals(date, output.eatByDate);
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
        Date date = format.parse("01.01.2015");
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
        assertEquals(date, output.eatByDate);
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
        Date date = format.parse("01.01.2015");
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
        assertEquals(date, output.eatByDate);
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
        Date date = format.parse("01.01.2015");
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
        assertEquals(date, output.eatByDate);
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
        Date date = format.parse("01.01.2015");
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
        assertEquals(date, output.eatByDate);
        assertEquals(0, output.buys);
        assertEquals(0, output.registers);
        verify(dbManager).getFood(food.name);
        verify(dbManager).getLocationsForFoodType(food.id);
        verify(dbManager).getLocations(location.name);
        verify(reader).nextDate(datePrompt);
        verify(reader).next(foodPrompt);
        verify(reader).next(locationPrompt);
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