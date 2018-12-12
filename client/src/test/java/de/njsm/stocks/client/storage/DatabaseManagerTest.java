package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.MockData;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.init.upgrade.Version;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.FoodItemView;
import de.njsm.stocks.common.data.view.FoodView;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.*;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
        Update inputItem = new Update();
        inputItem.table = "Location";
        inputItem.lastUpdate = Instant.now();
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
        expectedOutput.add(new User(2, "Jack"));
        expectedOutput.add(new User(1, "John"));
        expectedOutput.add(new User(3, "Juliette"));

        List<User> output = uut.getUsers();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void gettingFilteredUsersWorks() throws DatabaseException {
        User user = new User(1, "John");

        List<User> output = uut.getUsers(user.name);

        assertEquals(1, output.size());
        assertTrue(output.stream().anyMatch(u -> u.equals(user)));
    }

    @Test
    public void writingUsersWorks() throws DatabaseException {
        List<User> input = new LinkedList<>();
        User inputItem = new User(2, "John");
        input.add(inputItem);

        uut.writeUsers(input);

        List<User> output = uut.getUsers();
        assertThat(output, is(input));
    }

    @Test
    public void gettingDevicesWorks() throws DatabaseException {
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(5, "Desktop-PC", "John", 1));
        expectedOutput.add(new UserDeviceView(4, "Laptop", "John", 1));
        expectedOutput.add(new UserDeviceView(7, "Laptop", "Juliette", 3));
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette", 3));
        expectedOutput.add(new UserDeviceView(6, "PC-Work", "Jack", 2));

        List<UserDeviceView> output = uut.getDevices();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void gettingFilteredDevicesWorks() throws DatabaseException {
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette", 3));

        List<UserDeviceView> output = uut.getDevices("Mobile");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void writingDevicesWorks() throws DatabaseException {
        List<UserDevice> input = new LinkedList<>();
        input.add(new UserDevice(1, "Mobile", 1));
        input.add(new UserDevice(2, "Mobile", 2));
        input.add(new UserDevice(3, "Mobile", 3));
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John", 1));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack", 2));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette", 3));

        uut.writeDevices(input);

        List<UserDeviceView> result = uut.getDevices();
        assertThat(result, is(expectedOutput));
    }

    @Test
    public void testGettingLocations() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(4, "Basement"));
        expectedOutput.add(new Location(2, "Cupboard"));
        expectedOutput.add(new Location(3, "Cupboard"));
        expectedOutput.add(new Location(1, "Fridge"));

        List<Location> output = uut.getLocations();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingFilteredLocations() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(2, "Cupboard"));
        expectedOutput.add(new Location(3, "Cupboard"));

        List<Location> output = uut.getLocations("Cupboard");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingLocationsForFoodType() throws Exception {
        List<Location> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Location(3, "Cupboard"));
        expectedOutput.add(new Location(4, "Basement"));

        List<Location> output = uut.getLocationsForFoodType(7);

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingLocations() throws Exception {
        List<Location> input = new LinkedList<>();
        input.add(new Location(4, "Basement"));
        input.add(new Location(3, "Cupboard"));

        uut.writeLocations(input);

        List<Location> output = uut.getLocations();
        assertThat(output, is(input));
    }

    @Test
    public void testGettingAllFood() throws Exception {
        List<Food> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Food(7, "Apple juice"));
        expectedOutput.add(new Food(1, "Beer"));
        expectedOutput.add(new Food(3, "Bread"));
        expectedOutput.add(new Food(2, "Carrot"));
        expectedOutput.add(new Food(4, "Milk"));
        expectedOutput.add(new Food(6, "Raspberry jam"));
        expectedOutput.add(new Food(5, "Yoghurt"));

        List<Food> output = uut.getFood();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testGettingFilteredFood() throws Exception {
        List<Food> expectedOutput = new LinkedList<>();
        expectedOutput.add(new Food(4, "Milk"));

        List<Food> output = uut.getFood("Milk");

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingFood() throws Exception {
        List<Food> input = new LinkedList<>();
        input.add(new Food(4, "Milk"));

        uut.writeFood(input);

        List<Food> output = uut.getFood();
        assertThat(output, is(input));
    }

    @Test
    public void testGettingFoodItems() throws Exception {
        List<FoodItem> expectedOutput = new LinkedList<>();
        expectedOutput.add(new FoodItem(8, Instant.parse("1970-01-08T00:00:00.00Z"), 7, 3, 3, 3));
        expectedOutput.add(new FoodItem(9, Instant.parse("1970-01-09T00:00:00.00Z"), 7, 4, 3, 3));

        List<FoodItem> output = uut.getItems(7);

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void testWritingFoodItems() throws Exception {
        List<FoodItem> input = new LinkedList<>();
        input.add(new FoodItem(8, Instant.ofEpochMilli(0), 7, 3, 3, 3));
        input.add(new FoodItem(9, Instant.ofEpochMilli(0), 7, 4, 3, 3));

        uut.writeFoodItems(input);

        List<FoodItem> output = uut.getItems(7);
        assertThat(output, is(input));
    }

    @Test
    public void testGettingNextItem() throws Exception {
        FoodItem item1 = new FoodItem(3, Instant.parse("1970-01-03T00:00:00.00Z"), 3, 2, 1, 1);
        FoodItem item2 = new FoodItem(1, Instant.parse("1970-01-01T00:00:00.00Z"), 1, 1, 2, 2);
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
        FoodView item = new FoodView(new Food(7, "Apple juice"));
        item.add(new FoodItemView("Basement", "Juliette", "Mobile", Instant.parse("1970-01-09T00:00:00.00Z")));
        expectedOutput.add(item);

        List<FoodView> output = uut.getItems("", "Basement");

        assertEquals(expectedOutput, output);
    }

    @Test
    public void testGettingFoodOfLocationAndUser() throws Exception {
        List<FoodView> expectedOutput = new LinkedList<>();
        FoodView item = new FoodView(new Food(7, "Apple juice"));
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
    public void noRollbackOnNullConnection() {
        DatabaseManager.rollback(null);
    }

    @Test
    public void testRollback() throws SQLException {
        Connection c = Mockito.mock(Connection.class);

        DatabaseManager.rollback(c);

        Mockito.verify(c).rollback();
        Mockito.verifyNoMoreInteractions(c);
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
