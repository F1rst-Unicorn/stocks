package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.threeten.bp.Instant;
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
    public void setup() throws Exception {
        outMock = mock(ScreenWriter.class);
        inMock = mock(InputReader.class);
        uut = new Selector(outMock, inMock);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(outMock);
        verifyNoMoreInteractions(inMock);
    }

    @Test
    public void selectingEmptyUserListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectUser(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No users found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectFood(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No food found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodItemListThrowsException() throws Exception {
        try {
            uut.selectItem(Collections.emptyList());
            fail();
        } catch (InputException e) {
            assertEquals("No food items found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyLocationListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectLocation(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No locations found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyUserDeviceListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectDevice(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No devices found", e.getMessage());
        }
    }
    @Test
    public void selectingSingleUserListGivesItem() throws Exception {
        User expectedOutput = new User(3, "Jack");
        List<User> list = Collections.singletonList(expectedOutput);

        User output = uut.selectUser(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleFoodListGivesItem() throws Exception {
        String name = "some name";
        Food expectedOutput = new Food(3, "Beer");
        List<Food> list = Collections.singletonList(expectedOutput);

        Food output = uut.selectFood(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleFoodItemListGivesItem() throws Exception {
        FoodItem expectedOutput = new FoodItem(3, Instant.now(), 1, 2, 3, 4);
        List<FoodItem> list = Collections.singletonList(expectedOutput);

        FoodItem output = uut.selectItem(list);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleLocationListGivesItem() throws Exception {
        Location expectedOutput = new Location(3, "Fridge");
        List<Location> list = Collections.singletonList(expectedOutput);

        Location output = uut.selectLocation(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingSingleUserDeviceListGivesItem() throws Exception {
        UserDeviceView expectedOutput = new UserDeviceView(3, "Mobile", "Jack", 1);
        List<UserDeviceView> list = Collections.singletonList(expectedOutput);

        UserDeviceView output = uut.selectDevice(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

    @Test
    public void selectingBetweenSeveralGivesListToChoose() throws Exception {
        User user1 = new User(1, "Jack");
        User user2 = new User(2, "Jack");
        User user3 = new User(3, "Jack");
        User user4 = new User(5, "Jack");
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
    public void invalidSelectionThrowsException() throws Exception {
        String name = "Jack";
        User user1 = new User(1, name);
        User user2 = new User(2, name);
        User user3 = new User(3, name);
        User user4 = new User(5, name);
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
