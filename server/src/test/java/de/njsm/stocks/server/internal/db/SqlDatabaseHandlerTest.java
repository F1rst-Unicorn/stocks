package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.MockAuthAdmin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDatabaseHandlerTest {

    @Before
    public void resetDatabase() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();
    }

    @Test
    public void testAddingUser() throws IOException, SQLException {
        UserFactory factory = new UserFactory();
        User input = new User();
        Config c = new Config();
        Data[] output;
        int expectedUserCount = 3;
        boolean[] hits = new boolean[expectedUserCount];
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        input.name = "Mike";


        uut.add(input);


        output = uut.get(factory);
        Assert.assertEquals(expectedUserCount, output.length);
        for (Data d : output) {
            if (d instanceof User) {
                if (((User) d).name.equals("Mike")) {
                    hits[0] = true;
                }
                if (((User) d).name.equals("Bob")) {
                    hits[1] = true;
                }
                if (((User) d).name.equals("Alice")) {
                    hits[2] = true;
                }
            } else {
                throw new IllegalArgumentException("Got non-user data entry");
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testRenamingFood() throws IOException, SQLException {
        FoodFactory factory = new FoodFactory();
        Food input = new Food();
        Config c = new Config();
        Data[] output;
        int expectedHits = 3;
        boolean[] hits = new boolean[expectedHits];
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        // rename carrots to peppers
        input.id = 1;


        uut.rename(input, "Pepper");


        output = uut.get(factory);
        Assert.assertEquals(expectedHits, output.length);
        for (Data d : output) {
            if (d instanceof Food) {
                if (((Food) d).name.equals("Pepper")) {
                    hits[0] = true;
                }
                if (((Food) d).name.equals("Beer")) {
                    hits[1] = true;
                }
                if (((Food) d).name.equals("Cheese")) {
                    hits[2] = true;
                }
            } else {
                throw new IllegalArgumentException("Got non-food data entry");
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testRemovingFood() throws IOException, SQLException {
        FoodFactory factory = new FoodFactory();
        Food input = new Food();
        Config c = new Config();
        Data[] output;
        int expectedCount = 2;
        boolean[] hits = new boolean[expectedCount];
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        input.id = 1;


        uut.remove(input);


        output = uut.get(factory);
        Assert.assertEquals(expectedCount, output.length);
        for (Data d : output) {
            if (d instanceof Food) {
                if (((Food) d).name.equals("Beer")) {
                    hits[0] = true;
                }
                if (((Food) d).name.equals("Cheese")) {
                    hits[1] = true;
                }
            } else {
                throw new IllegalArgumentException("Got non-food data entry");
            }
        }

        assertArrayTrue(hits);
    }

    @Test
    public void testAddingDevice() throws IOException, SQLException {
        Config c = new Config();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        UserDevice input = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        int expectedDevices = 5;
        boolean[] hits = new boolean[expectedDevices];
        int deviceId = -1;
        boolean ticketPresent = false;
        Data[] output;
        input.name = "new device";
        input.userId = 1;


        uut.addDevice(input);


        output = uut.get(factory);
        Assert.assertEquals(expectedDevices, output.length);
        for (Data d : output) {
            if (d instanceof UserDevice) {
                if (((UserDevice) d).name.equals("mobile")) {
                    hits[0] = true;
                }
                if (((UserDevice) d).name.equals("mobile2")) {
                    hits[1] = true;
                }
                if (((UserDevice) d).name.equals("laptop")) {
                    hits[2] = true;
                }
                if (((UserDevice) d).name.equals("pending_device")) {
                    hits[3] = true;
                }
                if (((UserDevice) d).name.equals("new device")) {
                    hits[4] = true;
                    deviceId = ((UserDevice) d).id;
                }
            } else {
                throw new IllegalArgumentException("Got non-device data entry");
            }
        }
        assertArrayTrue(hits);

        Ticket[] tickets = getTickets();
        Assert.assertEquals(2, tickets.length);

        for (Ticket t : tickets) {
            if (t.deviceId == deviceId) {
                ticketPresent = true;
            }
        }

        Assert.assertTrue(ticketPresent);
    }

    @Test
    public void testGettingData() throws IOException, SQLException {
        Config c = new Config();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        FoodFactory factory = new FoodFactory();
        Data[] output;
        int expectedCount = 3;
        boolean[] hits = new boolean[expectedCount];


        output = uut.get(factory);


        Assert.assertEquals(expectedCount, output.length);
        for (Data d : output) {
            if (d instanceof Food) {
                Food f = (Food) d;
                if (f.name.equals("Carrot")) {
                    hits[0] = true;
                }
                if (f.name.equals("Beer")) {
                    hits[1] = true;
                }
                if (f.name.equals("Cheese")) {
                    hits[2] = true;
                }
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testMovingItems() throws IOException, SQLException {
        Config c = new Config();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        Data[] output;
        FoodItemFactory factory = new FoodItemFactory();
        FoodItem i = new FoodItem();
        int newLoc = 2; // from fridge to cupboard
        boolean itemMoved = false;
        i.id = 1;


        uut.moveItem(i, newLoc);


        output = uut.get(factory);
        Assert.assertEquals(3, output.length);
        for (Data d : output) {
            if (d instanceof FoodItem) {
                FoodItem f = (FoodItem) d;
                if (f.id == i.id && f.storedIn == newLoc) {
                    itemMoved = true;
                }
            }
        }

        Assert.assertTrue(itemMoved);
    }

    @Test
    public void testRemoveDevice() throws IOException, SQLException {
        MockConfig c = new MockConfig();
        MockAuthAdmin ca = (MockAuthAdmin) c.getCertAdmin();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        UserDevice d = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        Data[] output;
        int deviceCount = 3;
        boolean[] hits = new boolean[deviceCount];
        d.id = 2;


        uut.removeDevice(d);


        output = uut.get(factory);
        List<Integer> revokedIds = ca.getRevokedIds();
        Assert.assertEquals(1, revokedIds.size());
        Assert.assertEquals(d.id, (int) revokedIds.get(0));
        Assert.assertEquals(deviceCount, output.length);
        for (Data data : output) {
            if (data instanceof UserDevice) {
                UserDevice dev = (UserDevice) data;
                if (dev.name.equals("mobile")) {
                    hits[0] = true;
                }
                if (dev.name.equals("laptop")) {
                    hits[1] = true;
                }
                if (dev.name.equals("pending_device")) {
                    hits[2] = true;
                }
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testRemoveUser() throws IOException, SQLException {
        MockConfig c = new MockConfig();
        MockAuthAdmin ca = (MockAuthAdmin) c.getCertAdmin();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        User u = new User();
        UserFactory factory = new UserFactory();
        UserDeviceFactory devFactory = new UserDeviceFactory();
        Data[] output;
        int deviceCount = 2;
        boolean[] hits = new boolean[deviceCount];
        u.id = 1;


        uut.removeUser(u);


        output = uut.get(factory);
        List<Integer> revokedIds = ca.getRevokedIds();
        Assert.assertEquals(2, revokedIds.size());
        Assert.assertTrue(revokedIds.contains(1));
        Assert.assertTrue(revokedIds.contains(2));
        Assert.assertEquals(1, output.length);
        Assert.assertEquals(2, ((User) output[0]).id);

        output = uut.get(devFactory);
        Assert.assertEquals(deviceCount, output.length);
        for (Data d : output) {
            if (d instanceof UserDevice) {
                UserDevice dev = (UserDevice) d;
                if (dev.name.equals("laptop")) {
                    hits[0] = true;
                }
                if (dev.name.equals("pending_device")) {
                    hits[1] = true;
                }
            }
        }
        assertArrayTrue(hits);
    }

    private Ticket[] getTickets() throws SQLException {
        String sqlString = "SELECT * FROM Ticket";
        Connection c = DatabaseHelper.getConnection();
        PreparedStatement s = c.prepareStatement(sqlString);
        ResultSet res = s.executeQuery();
        ArrayList<Ticket> list = new ArrayList<>();

        while (res.next()) {
            Ticket t = new Ticket();
            t.ticket = res.getString("ticket");
            t.deviceId = res.getInt("belongs_device");
            list.add(t);
        }
        return list.toArray(new Ticket[list.size()]);
    }

    private void assertArrayTrue(boolean[] array) {
        for (boolean b : array) {
            Assert.assertTrue(b);
        }
    }
}
