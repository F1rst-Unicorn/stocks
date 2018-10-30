package de.njsm.stocks.server.v1.internal.db;

import de.njsm.stocks.server.v1.internal.data.*;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.DbTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SqlDatabaseHandlerTest extends DbTestCase {

    private SqlDatabaseHandler uut;

    @Before
    public void setup() {
        uut = new SqlDatabaseHandler(getConnectionFactory(),
                getNewResourceIdentifier());
    }

    @Test
    public void timestampsAreReadInUtc() {
        Instant expected = Instant.ofEpochMilli(0);

        Data[] result = uut.get(FoodItemFactory.f);

        assertEquals(expected, ((FoodItem) result[0]).eatByDate);
    }

    @Test
    public void testAddingUser() {
        UserFactory factory = new UserFactory();
        User input = new User();
        Data[] output;
        int expectedUserCount = 4;
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
                if (((User) d).name.equals("Jack")) {
                    hits[3] = true;
                }
            } else {
                throw new IllegalArgumentException("Got non-user data entry");
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testRenamingFood() {
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
    public void testRemovingFood() {
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
    public void testAddingDevice() {
        UserDevice input = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        int expectedDevices = 5;
        boolean[] hits = new boolean[expectedDevices];
        Data[] output;
        input.name = "new device";
        input.userId = 1;


        uut.add(input);


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
                }
            } else {
                throw new IllegalArgumentException("Got non-device data entry");
            }
        }
        assertArrayTrue(hits);
    }

    @Test
    public void testGettingData() {
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
    public void testMovingItems() {
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
    public void testRemoveDevice() {
        UserDevice d = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        Data[] output;
        int deviceCount = 3;
        boolean[] hits = new boolean[deviceCount];
        d.id = 2;


        uut.remove(d);


        output = uut.get(factory);
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
    public void testRemoveUser() {
        User u = new User(3, "Bob");
        UserFactory factory = new UserFactory();
        Data[] output;

        uut.remove(u);

        output = uut.get(factory);
        assertEquals(2, output.length);
        assertEquals(1, ((User) output[0]).id);
        assertEquals(2, ((User) output[1]).id);
    }

    @Test
    public void testGettingTicket() {
        String ticketValue = "AAAA";
        int expectedDeviceId = 3;

        ServerTicket output = uut.getTicket(ticketValue);

        assertEquals(output.deviceId, expectedDeviceId);
    }

    @Test
    public void gettingInvalidTicketReturnsNull() {
        String ticketValue = "not in the db";

        ServerTicket output = uut.getTicket(ticketValue);

        assertNull(output);
    }

    @Test
    public void testGettingPrincipalsForTicket() {
        Principals expected = new Principals("Alice", "laptop", 2, 3);

        Principals actual = uut.getPrincipalsForTicket("AAAA");

        assertEquals(expected, actual);
    }

    @Test
    public void principalsInvalidTicketReturnsNull() {
        Principals actual = uut.getPrincipalsForTicket("not in the db");

        assertNull(actual);
    }

    private void assertArrayTrue(boolean[] array) {
        for (boolean b : array) {
            Assert.assertTrue(b);
        }
    }

    @Test
    public void testGettingDeviceIdsOfUser() {
        List<Integer> expected = new ArrayList<>();
        expected.add(3);
        expected.add(4);

        List<Integer> actual = uut.getDeviceIdsOfUser(new User(2, "Alice"));

        assertEquals(expected, actual);
    }
}
