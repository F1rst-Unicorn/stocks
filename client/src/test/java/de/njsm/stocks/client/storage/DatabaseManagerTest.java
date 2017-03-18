package de.njsm.stocks.client.storage;

import de.njsm.stocks.common.data.Update;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.common.data.view.UserDeviceView;
import org.junit.*;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
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
    public void gettingUpdatesWorks() throws DatabaseException {

        List<Update> updates = uut.getUpdates();

        Assert.assertEquals(5, updates.size());
        for (Update u : updates) {
            Assert.assertEquals("Wrong for " + u.table, new Date(0L), u.lastUpdate);
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
        inputItem.lastUpdate = new Date();
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
            assertEquals(new Date(0L), u.lastUpdate);
        }
    }

    @Test
    public void gettingAllUsersWorks() throws DatabaseException {
        List<User> expectedOutput = new LinkedList<>();
        expectedOutput.add(new User(1, "John"));
        expectedOutput.add(new User(2, "Jack"));
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
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John"));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack"));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette"));
        expectedOutput.add(new UserDeviceView(4, "Laptop", "John"));
        expectedOutput.add(new UserDeviceView(5, "Desktop-PC", "John"));
        expectedOutput.add(new UserDeviceView(6, "PC-Work", "Jack"));
        expectedOutput.add(new UserDeviceView(7, "Laptop", "Juliette"));

        List<UserDeviceView> output = uut.getDevices();

        assertThat(output, is(expectedOutput));
    }

    @Test
    public void gettingFilteredDevicesWorks() throws DatabaseException {
        List<UserDeviceView> expectedOutput = new LinkedList<>();
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John"));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack"));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette"));

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
        expectedOutput.add(new UserDeviceView(1, "Mobile", "John"));
        expectedOutput.add(new UserDeviceView(2, "Mobile", "Jack"));
        expectedOutput.add(new UserDeviceView(3, "Mobile", "Juliette"));

        uut.writeDevices(input);

        List<UserDeviceView> result = uut.getDevices();
        assertThat(result, is(expectedOutput));
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
}
