package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class SqlDatabaseHandlerTest {

    @Test
    public void testAddingUser() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();
        UserFactory factory = new UserFactory();
        User input = new User();
        Config c = new Config();
        Data[] output;
        int userCount = 0;
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);

        input.name = "Mike";

        uut.add(input);

        output = uut.get(factory);

        Assert.assertEquals(3, output.length);
        for (Data d : output) {
            if (d instanceof User) {
                if (((User) d).name.equals("Mike")) {
                    userCount++;
                }
                if (((User) d).name.equals("Bob")) {
                    userCount++;
                }
                if (((User) d).name.equals("Alice")) {
                    userCount++;
                }
            } else {
                throw new IllegalArgumentException("Got non-user data entry");
            }
        }

        Assert.assertEquals(3, userCount);
    }

    @Test
    public void testRenamingFood() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();
        FoodFactory factory = new FoodFactory();
        Food input = new Food();
        Config c = new Config();
        Data[] output;
        int foodCount = 0;
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);

        // rename carrots to peppers
        input.id = 1;

        uut.rename(input, "Pepper");

        output = uut.get(factory);

        Assert.assertEquals(3, output.length);
        for (Data d : output) {
            if (d instanceof Food) {
                if (((Food) d).name.equals("Pepper")) {
                    foodCount++;
                }
                if (((Food) d).name.equals("Beer")) {
                    foodCount++;
                }
                if (((Food) d).name.equals("Cheese")) {
                    foodCount++;
                }
            } else {
                throw new IllegalArgumentException("Got non-food data entry");
            }
        }

        Assert.assertEquals(3, foodCount);
    }

    @Test
    public void testRemovingFood() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();
        FoodFactory factory = new FoodFactory();
        Food input = new Food();
        Config c = new Config();
        Data[] output;
        int foodCount = 0;
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);

        input.id = 1;

        uut.remove(input);

        output = uut.get(factory);

        Assert.assertEquals(2, output.length);
        for (Data d : output) {
            if (d instanceof Food) {
                if (((Food) d).name.equals("Beer")) {
                    foodCount++;
                }
                if (((Food) d).name.equals("Cheese")) {
                    foodCount++;
                }
            } else {
                throw new IllegalArgumentException("Got non-food data entry");
            }
        }

        Assert.assertEquals(2, foodCount);
    }

    @Test
    public void testAddingDevice() throws IOException, SQLException {
        DatabaseHelper.resetSampleData();
        Config c = new Config();
        SqlDatabaseHandler uut = new SqlDatabaseHandler(c);
        UserDevice input = new UserDevice();
        UserDeviceFactory factory = new UserDeviceFactory();
        int deviceCount = 0;
        int deviceId = -1;
        boolean ticketPresent = false;
        Data[] output;

        input.name = "new device";
        input.userId = 1;

        uut.addDevice(input);

        output = uut.get(factory);

        Assert.assertEquals(4, output.length);
        for (Data d : output) {
            if (d instanceof UserDevice) {
                if (((UserDevice) d).name.equals("mobile")) {
                    deviceCount++;
                }
                if (((UserDevice) d).name.equals("laptop")) {
                    deviceCount++;
                }
                if (((UserDevice) d).name.equals("pending_device")) {
                    deviceCount++;
                }
                if (((UserDevice) d).name.equals("new device")) {
                    deviceCount++;
                    deviceId = ((UserDevice) d).id;
                }
            } else {
                throw new IllegalArgumentException("Got non-device data entry");
            }
        }
        Assert.assertEquals(4, deviceCount);

        Ticket[] tickets = getTickets();
        Assert.assertEquals(2, tickets.length);

        for (Ticket t : tickets) {
            if (t.deviceId == deviceId) {
                ticketPresent = true;
            }
        }

        Assert.assertTrue(ticketPresent);
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
}
