package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ScreenWriterTest {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private ScreenWriter uut;

    private PrintStream mockStream;

    private ArgumentCaptor<String> captor;

    @Before
    public void setup() throws Exception {
        mockStream = mock(PrintStream.class);
        uut = new ScreenWriter(mockStream);
        captor = ArgumentCaptor.forClass(String.class);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(mockStream);
    }

    @Test
    public void writingStringWorks() throws Exception {
        String message = "This is a nice message";

        uut.println(message);

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingLocationWorks() throws Exception {
        Location location = new Location(4, "Fridge");
        String expectedResult = "\t" + location.id + ": " + location.name;

        uut.printLocation(location);

        verify(mockStream).println(captor.capture());
        assertEquals(expectedResult, captor.getValue());
    }

    @Test
    public void printingEmptyLocationListShowsConstantMessage() throws Exception {
        String message = "\tNo locations there...";

        uut.printLocations("Unused headline", Collections.emptyList());

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingLocationListWorks() throws Exception {
        List<Location> list = new LinkedList<>();
        list.add(new Location(2, "Basement"));
        list.add(new Location(3, "Fridge"));
        String headline = "Some headline";
        String item1 = "\t" + list.get(0).id + ": " + list.get(0).name;
        String item2 = "\t" + list.get(1).id + ": " + list.get(1).name;

        uut.printLocations(headline, list);

        verify(mockStream, times(3)).println(captor.capture());
        assertEquals(headline, captor.getAllValues().get(0));
        assertEquals(item1, captor.getAllValues().get(1));
        assertEquals(item2, captor.getAllValues().get(2));
    }

    @Test
    public void printingFoodWorks() throws Exception {
        Food food = new Food(4, "Beer");
        String expectedResult = "\t" + food.id + ": " + food.name;

        uut.printFood(food);

        verify(mockStream).println(captor.capture());
        assertEquals(expectedResult, captor.getValue());
    }

    @Test
    public void printingEmptyFoodListShowsConstantMessage() throws Exception {
        String message = "\tNo food there...";

        uut.printFood("Unused headline", Collections.emptyList());

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingFoodListWorks() throws Exception {
        List<Food> list = new LinkedList<>();
        list.add(new Food(2, "Bread"));
        list.add(new Food(3, "Beer"));
        String headline = "Some headline";
        String item1 = "\t" + list.get(0).id + ": " + list.get(0).name;
        String item2 = "\t" + list.get(1).id + ": " + list.get(1).name;

        uut.printFood(headline, list);

        verify(mockStream, times(3)).println(captor.capture());
        assertEquals(headline, captor.getAllValues().get(0));
        assertEquals(item1, captor.getAllValues().get(1));
        assertEquals(item2, captor.getAllValues().get(2));
    }

    @Test
    public void printingItemWorks() throws Exception {
        FoodItem item = new FoodItem();
        item.id = 4;
        item.eatByDate = new Date(0L);
        String expectedResult = "\t\t" + item.id + ": " + format.format(item.eatByDate);

        uut.printItem(item);

        verify(mockStream).println(captor.capture());
        assertEquals(expectedResult, captor.getValue());
    }

    @Test
    public void printingEmptyFoodItemListShowsConstantMessage() throws Exception {
        String message = "\tNo items there...";

        uut.printItems("Unused headline", Collections.emptyList());

        verify(mockStream).println(captor.capture());
        assertEquals(message, captor.getValue());
    }

    @Test
    public void printingFoodItemListWorks() throws Exception {
        List<FoodItem> list = new LinkedList<>();
        FoodItem item1 = new FoodItem();
        item1.id = 3;
        item1.eatByDate = new Date();
        FoodItem item2 = new FoodItem();
        item2.id = 3;
        item2.eatByDate = new Date();
        list.add(item1);
        list.add(item2);
        String headline = "Some headline";
        String item1Text = "\t\t" + list.get(0).id + ": " + format.format(list.get(0).eatByDate);
        String item2Text = "\t\t" + list.get(1).id + ": " + format.format(list.get(1).eatByDate);

        uut.printItems(headline, list);

        verify(mockStream, times(3)).println(captor.capture());
        assertEquals(headline, captor.getAllValues().get(0));
        assertEquals(item1Text, captor.getAllValues().get(1));
        assertEquals(item2Text, captor.getAllValues().get(2));
    }

    @Test
    public void testPrintingUser() throws Exception {
        User input = new User(3, "Jack");
        String expectedOutput = "\t" + input.id + ": " + input.name;

        uut.printUser(input);

        verify(mockStream).println(expectedOutput);
    }

    @Test
    public void printingEmptyUserListGivesMessage() throws Exception {
        String message = "No users there...";

        uut.printUsers("Unused headline", Collections.emptyList());

        verify(mockStream).println(message);
    }

    @Test
    public void printingUsersGivesUserList() throws Exception {
        String headline = "some headline";
        List<User> users = new LinkedList<>();
        User user1 = new User(3, "Jack");
        User user2 = new User(4, "Juliette");
        users.add(user1);
        users.add(user2);

        uut.printUsers(headline, users);

        verify(mockStream).println(headline);
        verify(mockStream).println("\t" + user1.id + ": " + user1.name);
        verify(mockStream).println("\t" + user2.id + ": " + user2.name);
    }
}
