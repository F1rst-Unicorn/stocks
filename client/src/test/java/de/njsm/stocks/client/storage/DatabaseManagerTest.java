package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.MockData;
import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.FoodItemView;
import de.njsm.stocks.client.business.data.view.FoodView;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.init.upgrade.Version;
import org.junit.*;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseManagerTest {

    private DatabaseManager uut;

    private static DatabaseHelper helper;

    @BeforeClass
    public static void setupClass() throws Exception {
        helper = new DatabaseHelper();
        helper.setupDatabase();
    }

    @Before
    public void setup() throws Exception {
        uut = new DatabaseManager();
        helper.fillData();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        helper.removeDatabase();
    }

    @Test
    public void gettingUpdatesWorks() throws Exception {

        List<Update> updates = uut.getUpdates();

        Assert.assertEquals(5, updates.size());
        for (Update u : updates) {
            Assert.assertEquals("Wrong for " + u.table, Instant.ofEpochMilli(0), u.lastUpdate);
        }
        assertTrue(updates.stream().anyMatch(u -> u.table.equals("Location")));
        assertTrue(updates.stream().anyMatch(u -> u.table.equals("User")));
        assertTrue(updates.stream().anyMatch(u -> u.table.equals("User_device")));
        assertTrue(updates.stream().anyMatch(u -> u.table.equals("Food")));
        assertTrue(updates.stream().anyMatch(u -> u.table.equals("Food_item")));
    }

    @Test
    public void writingUpdatesWorks() throws DatabaseException {
        Update inputItem = new Update("Location", Instant.now());
        List<Update> input = new LinkedList<>();
        input.add(inputItem);

        uut.writeUpdates(input);

        List<Update> output = uut.getUpdates();
        for (Update u : output) {
            if (u.table.equals(inputItem.table)) {
                assertEquals(inputItem.lastUpdate, u.lastUpdate);
            }
        }
    }

    @Test
    public void resettingUpdatesWorks() throws DatabaseException {

        uut.resetUpdates();

        List<Update> updates = uut.getUpdates();
        for (Update u : updates) {
            assertEquals(Instant.ofEpochMilli(0), u.lastUpdate);
        }
    }

    @Test
    public void gettingAllUsersWorks() throws DatabaseException {
        List<User> expectedOutput = new LinkedList<>();
        expectedOutput.add(new User(1, 6, "John"));
        expectedOutput.add(new User(2, 7, "Jack"));
        expectedOutput.add(new User(3, 8, "Juliette"));

        List<User> output = uut.getUsers();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void gettingFilteredUsersWorks() throws DatabaseException {
        User user = new User(1, 6, "John");

        List<User> output = uut.getUsers(user.name);

        assertEquals(1, output.size());
        assertTrue(output.stream().anyMatch(u -> u.equals(user)));
    }

    @Test
    public void writingUsersWorks() throws DatabaseException {
        List<User> input = new LinkedList<>();
        User inputItem = new User(2, 7, "John");
        input.add(inputItem);

        uut.writeUsers(input);

        List<User> output = uut.getUsers();
        assertThat(output, is(input));
    }

    @Test
    public void gettingDevicesWorks() throws DatabaseException {
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, 6, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, 7, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, 8, "Mobile", "Juliette", 3));
        expectedOutput.add(new UserDeviceView(4, 9, "Laptop", "John", 1));
        expectedOutput.add(new UserDeviceView(5, 10, "Desktop-PC", "John", 1));
        expectedOutput.add(new UserDeviceView(6, 11, "PC-Work", "Jack", 2));
        expectedOutput.add(new UserDeviceView(7, 12, "Laptop", "Juliette", 3));

        List<UserDeviceView> output = uut.getDevices();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void gettingFilteredDevicesWorks() throws DatabaseException {
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, 6, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, 7, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, 8, "Mobile", "Juliette", 3));

        List<UserDeviceView> output = uut.getDevices("Mobile");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void writingDevicesWorks() throws DatabaseException {
        List<UserDevice> input = new LinkedList<>();
        input.add(new UserDevice(1, 6, "Mobile", 1));
        input.add(new UserDevice(2, 7, "Mobile", 2));
        input.add(new UserDevice(3, 8, "Mobile", 3));
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, 6, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, 7, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, 8, "Mobile", "Juliette", 3));

        uut.writeDevices(input);

        List<UserDeviceView> result = uut.getDevices();
        assertThat(result, is(expectedOutput));
    }

    @Test
    public void testGettingLocations() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(1, 6, "Fridge"));
        expectedOutput.add(new Location(2, 7, "Cupboard"));
        expectedOutput.add(new Location(3, 8, "Cupboard"));
        expectedOutput.add(new Location(4, 9, "Basement"));

        List<Location> output = uut.getLocations();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingFilteredLocations() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(2, 7, "Cupboard"));
        expectedOutput.add(new Location(3, 8, "Cupboard"));

        List<Location> output = uut.getLocations("Cupboard");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingLocationsForFoodType() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(3, 8, "Cupboard"));
        expectedOutput.add(new Location(4, 9, "Basement"));

        List<Location> output = uut.getLocationsForFoodType(7);

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingLocations() throws Exception {
        List<Location> input = new LinkedList<>();
        input.add(new Location(3, 8, "Cupboard"));
        input.add(new Location(4, 9, "Basement"));

        uut.writeLocations(input);

        List<Location> output = uut.getLocations();
        assertThat(output, is(input));
    }

    @Test
    public void testGettingAllFood() throws Exception {
        List<Food> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Food(1, 6, "Beer"));
        expectedOutput.add(new Food(2, 7, "Carrot"));
        expectedOutput.add(new Food(3, 8, "Bread"));
        expectedOutput.add(new Food(4, 9, "Milk"));
        expectedOutput.add(new Food(5, 10, "Yoghurt"));
        expectedOutput.add(new Food(6, 11, "Raspberry jam"));
        expectedOutput.add(new Food(7, 12, "Apple juice"));

        List<Food> output = uut.getFood();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingFilteredFood() throws Exception {
        List<Food> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Food(4, 9, "Milk"));

        List<Food> output = uut.getFood("Milk");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingFood() throws Exception {
        List<Food> input = new LinkedList<>();
        input.add(new Food(4, 9, "Milk"));

        uut.writeFood(input);

        List<Food> output = uut.getFood();
        assertThat(output, is(input));
    }

    @Test
    public void testGettingFoodItems() throws Exception {
        List<FoodItem> expectedOutput = new LinkedList<>();
        expectedOutput.add(new FoodItem(8, 13, Instant.parse("1970-01-08T00:00:00.00Z"), 7, 3, 3, 3));
        expectedOutput.add(new FoodItem(9, 14, Instant.parse("1970-01-09T00:00:00.00Z"), 7, 4, 3, 3));

        List<FoodItem> output = uut.getItems(7);

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingFoodItems() throws Exception {
        List<FoodItem> input = new LinkedList<>();
        input.add(new FoodItem(8, 13, Instant.ofEpochMilli(0), 7, 3, 3, 3));
        input.add(new FoodItem(9, 14, Instant.ofEpochMilli(0), 7, 4, 3, 3));

        uut.writeFoodItems(input);

        List<FoodItem> output = uut.getItems(7);
        assertThat(output, is(input));
    }

    @Test
    public void testGettingNextItem() throws Exception {
        FoodItem item1 = new FoodItem(3, 8, Instant.parse("1970-01-03T00:00:00.00Z"), 3, 2, 1, 1);
        FoodItem item2 = new FoodItem(1, 6, Instant.parse("1970-01-01T00:00:00.00Z"), 1, 1, 2, 2);
        assertEquals(item1, uut.getNextItem(3));
        assertEquals(item2, uut.getNextItem(1));
    }

    @Test
    public void gettingItemOfEmptyFoodThrowsException() throws Exception {
        try {
            uut.getNextItem(2);
            fail();
        } catch (InputException e) {
            assertEquals("You don't have any...", e.getMessage());
        }
    }

    @Test
    public void testGettingFoodOfUser() throws Exception {
        List<FoodView> entireFood = MockData.getTestFoodInDatabase();
        FoodView item = entireFood.get(6);
        List<FoodView> expectedOutput = new LinkedList<>();
        expectedOutput.add(item);

        List<FoodView> output = uut.getItems("Juliette", "");

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testGettingAllFoodItems() throws Exception {

        List<FoodView> output = uut.getItems("", "");

        assertEquals(MockData.getTestFoodInDatabase(), output);
    }

    @Test
    public void testGettingFoodOfLocation() throws Exception {
        List<FoodView> expectedOutput = new LinkedList<>();
        FoodView item = new FoodView(new Food(7, 12, "Apple juice"));
        item.add(new FoodItemView("Basement", "Juliette", "Mobile", Instant.parse("1970-01-09T00:00:00.00Z")));
        expectedOutput.add(item);

        List<FoodView> output = uut.getItems("", "Basement");

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testGettingFoodOfLocationAndUser() throws Exception {
        List<FoodView> expectedOutput = new LinkedList<>();
        FoodView item = new FoodView(new Food(7, 12, "Apple juice"));
        item.add(new FoodItemView("Basement", "Juliette", "Mobile", Instant.parse("1970-01-09T00:00:00.00Z")));
        expectedOutput.add(item);

        List<FoodView> output = uut.getItems("Juliette", "Basement");

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testGettingItemsWithTooStrongAttributes() throws DatabaseException {
        List<FoodView> expectedOutput = new LinkedList<>();

        List<FoodView> output = uut.getItems("John", "Basement");

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testGettingVersionWithoutVersionTable() throws Exception {
        helper.runSqlCommand("DROP TABLE Config");

        Version output = uut.getDbVersion();

        assertEquals(Version.PRE_VERSIONED, output);
        helper.runSqlCommand("CREATE TABLE Config ( " +
                "`key` varchar(100) NOT NULL UNIQUE, " +
                "`value` varchar(100) NOT NULL, " +
                "PRIMARY KEY (`key`) " +
                ")");
        helper.runSqlCommand("INSERT INTO Config (key,value) VALUES ('db.version', '0.5.0')");
    }

    @Test
    public void testGettingVersionWithVersionTable() throws Exception {

        Version output = uut.getDbVersion();

        assertEquals(Version.V_0_5_0, output);
    }

    @Test
    public void noCloseOnNullConnection() {
        DatabaseManager.close(null);
    }

    @Test
    public void testClosing() throws SQLException {
        Connection c = Mockito.mock(Connection.class);

        DatabaseManager.close(c);

        Mockito.verify(c).close();
        Mockito.verifyNoMoreInteractions(c);
    }
}
