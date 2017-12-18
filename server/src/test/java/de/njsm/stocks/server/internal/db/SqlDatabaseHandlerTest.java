package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.MockAuthAdmin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SqlDatabaseHandlerTest {

    private Config c;
    private MockAuthAdmin ca;
    private SqlDatabaseHandler uut;

    @Before
    public void resetDatabase() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();

        c = new Config(System.getProperties());
        ca = new MockAuthAdmin();
        uut = new SqlDatabaseHandler(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                ca);
    }

    @Test
    public void timestampsAreReadInUtc() throws Exception {
        Instant expected = Instant.ofEpochMilli(0);

        Data[] result = uut.get(FoodItemFactory.f);

        assertEquals(expected, ((FoodItem) result[0]).eatByDate);
    }

    @Test
    public void testAddingUser() throws IOException, SQLException {
        UserFactory factory = new UserFactory();
        User input = new User();
        Data[] output;
        int expectedUserCount = 3;
        boolean[] hits = new boolean[expectedUserCount];
        input.name = "Mike";

        uut.add(input);

        output = uut.get(factory);
        assertEquals(expectedUserCount, output.length);
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
        Data[] output;
        int expectedHits = 3;
        boolean[] hits = new boolean[expectedHits];
        // rename carrots to peppers
        input.id = 1;


        uut.rename(input, "Pepper");


        output = uut.get(factory);
        assertEquals(expectedHits, output.length);
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
        Data[] output;
        int expectedCount = 2;
        boolean[] hits = new boolean[expectedCount];
        input.id = 1;


        uut.remove(input);


        output = uut.get(factory);
        assertEquals(expectedCount, output.length);
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
        assertEquals(expectedDevices, output.length);
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
        assertEquals(2, tickets.length);

        for (Ticket t : tickets) {
            if (t.deviceId == deviceId) {
                ticketPresent = true;
            }
        }

        Assert.assertTrue(ticketPresent);
    }

    @Test
    public void testGettingData() throws IOException, SQLException {
        FoodFactory factory = new FoodFactory();
        Data[] output;
        int expectedCount = 3;
        boolean[] hits = new boolean[expectedCount];


        output = uut.get(factory);


        assertEquals(expectedCount, output.length);
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
        Data[] output;
        FoodItemFactory factory = new FoodItemFactory();
        FoodItem i = new FoodItem();
        int newLoc = 2; // from fridge to cupboard
        boolean itemMoved = false;
        i.id = 1;


        uut.moveItem(i, newLoc);


        output = uut.get(factory);
        assertEquals(3, output.length);
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
        UserDevice d = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        Data[] output;
        int deviceCount = 3;
        boolean[] hits = new boolean[deviceCount];
        d.id = 2;


        uut.removeDevice(d);


        output = uut.get(factory);
        List<Integer> revokedIds = ca.getRevokedIds();
        assertEquals(1, revokedIds.size());
        assertEquals(d.id, (int) revokedIds.get(0));
        assertEquals(deviceCount, output.length);
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
        assertEquals(2, revokedIds.size());
        Assert.assertTrue(revokedIds.contains(1));
        Assert.assertTrue(revokedIds.contains(2));
        assertEquals(1, output.length);
        assertEquals(2, ((User) output[0]).id);

        output = uut.get(devFactory);
        assertEquals(deviceCount, output.length);
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

    @Test
    public void testRollbackWithNull() {
        uut.rollback(null);
    }

    @Test
    public void testRollbackWithConnection() throws SQLException {
        Connection con = Mockito.mock(Connection.class);

        uut.rollback(con);

        Mockito.verify(con).rollback();
        Mockito.verifyNoMoreInteractions(con);
    }

    @Test
    public void testRollbackWithException() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException("Mockito")).when(con).rollback();

        uut.rollback(con);

        Mockito.verify(con).rollback();
        Mockito.verifyNoMoreInteractions(con);
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
