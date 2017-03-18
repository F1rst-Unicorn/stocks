package de.njsm.stocks.client.storage;

import de.njsm.stocks.common.data.Update;
import de.njsm.stocks.common.data.User;
import org.junit.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    public void gettingUpdatesWorks() throws DatabaseException {

        List<Update> updates = uut.getUpdates();

        Assert.assertEquals(5, updates.size());
        for (Update u : updates) {
            Assert.assertEquals("Wrong for " + u.table, new Date(0L), u.lastUpdate);
        }
        Assert.assertTrue(updates.stream().anyMatch(u -> u.table.equals("Location")));
        Assert.assertTrue(updates.stream().anyMatch(u -> u.table.equals("User")));
        Assert.assertTrue(updates.stream().anyMatch(u -> u.table.equals("User_device")));
        Assert.assertTrue(updates.stream().anyMatch(u -> u.table.equals("Food")));
        Assert.assertTrue(updates.stream().anyMatch(u -> u.table.equals("Food_item")));
    }

    @Test
    public void writingUpdatesWorks() throws DatabaseException {
        Update inputItem = new Update();
        inputItem.table = "Location";
        inputItem.lastUpdate = new Date();
        List<Update> input = new LinkedList<>();
        input.add(inputItem);

        uut.writeUpdates(input);

        List<Update> output = uut.getUpdates();
        for (Update u : output) {
            if (u.table.equals(inputItem.table)) {
                Assert.assertEquals(inputItem.lastUpdate, u.lastUpdate);
            }
        }
    }

    @Test
    public void resettingUpdatesWorks() throws DatabaseException {

        uut.resetUpdates();

        List<Update> updates = uut.getUpdates();
        for (Update u : updates) {
            Assert.assertEquals(new Date(0L), u.lastUpdate);
        }
    }

    @Test
    public void gettingAllUsersWorks() throws DatabaseException {

        List<User> output = uut.getUsers();

        Assert.assertEquals(5, output.size());
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals("John")));
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals("Jack")));
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals("Juliette")));
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals("Jason")));
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals("Justin")));
    }
    
    @Test
    public void gettingFilteredUsersWorks() throws DatabaseException {
        String name = "John";
        
        List<User> output = uut.getUsers(name);
        
        Assert.assertEquals(1, output.size());
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals(name)));
    }

    @Test
    public void writingUsersWorks() throws DatabaseException {
        List<User> input = new LinkedList<>();
        User inputItem = new User();
        inputItem.name = "John";
        inputItem.id = 2;
        input.add(inputItem);

        uut.writeUsers(input);

        List<User> output = uut.getUsers();

        Assert.assertEquals(1, output.size());
        Assert.assertTrue(output.stream().anyMatch(u -> u.name.equals(inputItem.name)));
    }

    @Test
    public void noRollbackOnNullConnection() {
        DatabaseManager.rollback(null);
    }
}
