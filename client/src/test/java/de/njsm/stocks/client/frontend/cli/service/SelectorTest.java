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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
            assertEquals("No such user found: " + name, e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectFood(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No such food found: " + name, e.getMessage());
        }
    }

    @Test
    public void selectingEmptyFoodItemListThrowsException() throws Exception {
        try {
            uut.selectItem(Collections.emptyList());
            fail();
        } catch (InputException e) {
            assertEquals("No items found", e.getMessage());
        }
    }

    @Test
    public void selectingEmptyLocationListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectLocation(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No such location found: " + name, e.getMessage());
        }
    }

    @Test
    public void selectingEmptyUserDeviceListThrowsException() throws Exception {
        String name = "some name";
        try {
            uut.selectDevice(Collections.emptyList(), name);
            fail();
        } catch (InputException e) {
            assertEquals("No such device found: " + name, e.getMessage());
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
        FoodItem expectedOutput = new FoodItem(3, new Date(), 1, 2, 3, 4);
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
        UserDeviceView expectedOutput = new UserDeviceView(3, "Mobile", "Jack");
        List<UserDeviceView> list = Collections.singletonList(expectedOutput);

        UserDeviceView output = uut.selectDevice(list, expectedOutput.name);

        assertEquals(expectedOutput, output);
    }

}
