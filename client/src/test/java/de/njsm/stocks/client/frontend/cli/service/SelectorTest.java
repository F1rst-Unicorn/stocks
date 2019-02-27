package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.business.data.Food;
import de.njsm.stocks.client.business.data.FoodItem;
import de.njsm.stocks.client.business.data.Location;
import de.njsm.stocks.client.business.data.User;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.InputException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SelectorTest {

    private Selector uut;

    private ScreenWriter outMock;

    private InputReader inMock;

    @Before
    public void setup() {
        outMock = mock(ScreenWriter.class);
        inMock = mock(InputReader.class);
        uut = new Selector(outMock, inMock);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(outMock);
        verifyNoMoreInteractions(inMock);
    }

    @Test
    public void selectingEmptyUserListThrowsException() {
        String name = "some name";
        try {
            uut.selectUser(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No users found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodListThrowsException() {
        String name = "some name";
        try {
            uut.selectFood(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No food found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodItemListThrowsException() {
        try {
            uut.selectItem(Collections.emptyList());
            fail();
        } catch (InputException e) {
            assertEquals("No food items found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyLocationListThrowsException() {
        String name = "some name";
        try {
            uut.selectLocation(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No locations found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyUserDeviceListThrowsException() {
        String name = "some name";
        try {
            uut.selectDevice(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No devices found", e.getMessage());
        }
    }
    @Test
    public void selectingSingleUserListGivesItem() throws InputException {
        User expectedOutput = new User(3, 8, "Jack");
        List<User> list = Collections.singletonList(expectedOutput);

        User output = uut.selectUser(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleFoodListGivesItem() throws InputException {
        Food expectedOutput = new Food(3, 8, "Beer");
        List<Food> list = Collections.singletonList(expectedOutput);

        Food output = uut.selectFood(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleFoodItemListGivesItem() throws InputException {
        FoodItem expectedOutput = new FoodItem(3, 8, Instant.now(), 1, 2, 3, 4);
        List<FoodItem> list = Collections.singletonList(expectedOutput);

        FoodItem output = uut.selectItem(list);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleLocationListGivesItem() throws InputException {
        Location expectedOutput = new Location(3, 8, "Fridge");
        List<Location> list = Collections.singletonList(expectedOutput);

        Location output = uut.selectLocation(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleUserDeviceListGivesItem() throws InputException {
        UserDeviceView expectedOutput = new UserDeviceView(3, 8, "Mobile", "Jack", 1);
        List<UserDeviceView> list = Collections.singletonList(expectedOutput);

        UserDeviceView output = uut.selectDevice(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingBetweenSeveralGivesListToChoose() throws InputException {
        User user1 = new User(1, 6, "Jack");
        User user2 = new User(2, 7, "Jack");
        User user3 = new User(3, 8, "Jack");
        User user4 = new User(5, 10, "Jack");
        List<User> list = new LinkedList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        when(inMock.nextInt(anyString(), anyInt())).thenReturn(3);

        User output = uut.selectUser(list, user3.name);

        assertEquals(user3, output);
        verify(outMock).printDataList("Found more than one possibility: ",
                "users",
                list);
        verify(inMock).nextInt("Choose one ", 1);
    }

    @Test
    public void invalidSelectionThrowsException() {
        String name = "Jack";
        User user1 = new User(1, 6, name);
        User user2 = new User(2, 7, name);
        User user3 = new User(3, 8, name);
        User user4 = new User(5, 10, name);
        List<User> list = new LinkedList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        int invalidSelection = 4;
        when(inMock.nextInt(anyString(), anyInt())).thenReturn(invalidSelection);

        try {
            uut.selectUser(list, name);
            fail();
        } catch (InputException e) {
            assertEquals("You did an invalid selection", e.getMessage());
        }

        verify(outMock).printDataList("Found more than one possibility: ",
                "users",
                list);
        verify(inMock).nextInt("Choose one ", 1);

    }
}
