package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.auth.CertificateAdmin;
import de.njsm.stocks.internal.auth.UserContext;

import javax.ws.rs.Produces;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

public class SqlDatabaseHandler implements DatabaseHandler {

    protected String url;
    protected Config c;

    public SqlDatabaseHandler() {

        c = new Config();

        String address = System.getProperty("de.njsm.stocks.internal.db.databaseAddress");
        String port = System.getProperty("de.njsm.stocks.internal.db.databasePort");
        String name = System.getProperty("de.njsm.stocks.internal.db.databaseName");
        String user = System.getProperty("de.njsm.stocks.internal.db.databaseUsername");
        String password = System.getProperty("de.njsm.stocks.internal.db.databasePassword");

        url = String.format("jdbc:mariadb://%s:%s/%s?user=%s&password=%s",
                address,
                port,
                name,
                user,
                password);

    }

    private Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url);
    }

    public void addLocation(Location location) throws SQLException {
        String command="INSERT INTO Location (ID, name) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt = con.prepareStatement(command)) {

            sqlStmt.setString(1, location.name);
            sqlStmt.execute();
        }
    }

    public void removeLocation(int id) throws SQLException {
        String command="DELETE FROM Location WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt = con.prepareStatement(command)) {

            sqlStmt.setInt(1, id);
            sqlStmt.execute();
        }
    }

    public void renameLocation(int id, String new_name) throws SQLException {
        String command = "UPDATE Location SET name=? WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt = con.prepareStatement(command)) {

            sqlStmt.setString(1, new_name);
            sqlStmt.setInt(2, id);
            sqlStmt.execute();
        }
    }

    public void removeUser(int id) throws SQLException {

        String command="DELETE FROM User WHERE ID=?";
        String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
        CertificateAdmin ca = new Config().getCertAdmin();

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            con.setAutoCommit(false);
            // revoke all devices
            sqlQuery.setInt(1, id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()){
                ca.revokeCertificate(res.getInt("ID"));
            }

            sqlStmt.setInt(1, id);
            sqlStmt.execute();

            con.commit();
        }

    }

    public void removeDevice(int id) throws SQLException {
        CertificateAdmin ca = new Config().getCertAdmin();
        ca.revokeCertificate(id);

        String command="DELETE FROM User_device WHERE ID=?";
        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setInt(1, id);
            sqlStmt.execute();
        }
    }

    public void addFood(UserContext c, Food food) throws SQLException {
        String command="INSERT INTO Food (name) VALUES (?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setString(1, food.name);
            sqlStmt.execute();
        }
    }

    public void removeFood(UserContext c, int id) throws SQLException {
        String command="DELETE FROM Food WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setInt(1, id);
            sqlStmt.execute();
        }
    }

    public void renameFood(UserContext c, int id, String new_name) throws SQLException {
        String command="UPDATE Food SET name=? WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setString(1, new_name);
            sqlStmt.setInt(2, id);
            sqlStmt.execute();
        }
    }

    public void addFoodItem(UserContext c, FoodItem item) throws SQLException {
        String command="INSERT INTO Food_item (eat_by, of_type, stored_in, registers, buys) " +
                "VALUES (?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setDate(1, new java.sql.Date(item.eatByDate.getTime()));
            sqlStmt.setInt(2, item.ofType);
            sqlStmt.setInt(3, item.storedIn);
            sqlStmt.setInt(4, c.getDeviceId());
            sqlStmt.setInt(5, c.getId());
            sqlStmt.execute();
        }
    }

    public void removeFoodItem(UserContext c, int id) throws SQLException {
        String command="DELETE FROM Food_item WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setInt(1, id);
            sqlStmt.execute();
        }
    }

    public String getNewTicket() throws SQLException {

        String ticket = generateTicket();
        String command = "INSERT INTO Ticket (ticket, created_on) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt = con.prepareStatement(command)) {

            java.util.Date now = new java.util.Date();
            sqlStmt.setString(1, ticket);
            java.sql.Timestamp timestamp = new java.sql.Timestamp(now.getTime());
            sqlStmt.setTimestamp(2, timestamp);
            sqlStmt.execute();
        }

        return ticket;
    }

    public Location[] getLocations() throws SQLException {

        String query = "SELECT * " +
                       "FROM Location";
        try (
                Connection con = getConnection();
                PreparedStatement stmt = con.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            ArrayList<Location> result = new ArrayList<>();
            while (rs.next()) {
                Location l = new Location();
                l.id = rs.getInt("ID");
                l.name = rs.getString("name");
                result.add(l);
            }

            return result.toArray(new Location[result.size()]);
        }
    }

    public Food[] getFood() throws SQLException {
        String query = "SELECT * " +
                       "FROM Food";
        try (
                Connection con = getConnection();
                PreparedStatement stmt = con.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            ArrayList<Food> result = new ArrayList<>();
            while (rs.next()) {
                Food f = new Food();
                f.id = rs.getInt("ID");
                f.name = rs.getString("name");
                result.add(f);
            }

            return result.toArray(new Food[result.size()]);
        }
    }

    public User[] getUsers() throws SQLException {
        String query = "SELECT * " +
                "FROM User";
        try (
                Connection con = getConnection();
                PreparedStatement stmt = con.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            ArrayList<User> result = new ArrayList<>();
            while (rs.next()) {
                User u = new User();
                u.id = rs.getInt("ID");
                u.name = rs.getString("name");
                result.add(u);
            }

            return result.toArray(new User[result.size()]);
        }
    }

    public UserDevice[] getDevices() throws SQLException {
        String query = "SELECT * " +
                "FROM User_device";
        try (
                Connection con = getConnection();
                PreparedStatement stmt = con.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            ArrayList<UserDevice> result = new ArrayList<>();
            while (rs.next()) {
                UserDevice d = new UserDevice();
                d.id = rs.getInt("ID");
                d.name = rs.getString("name");
                d.userId = rs.getInt("belongs_to");
                d.lastUpdate = rs.getDate("last_poll");
                result.add(d);
            }

            return result.toArray(new UserDevice[result.size()]);
        }
    }

    public FoodItem[] getFoodItems() throws SQLException {
        String query = "SELECT * " +
                "FROM Food_item";
        try (
                Connection con = getConnection();
                PreparedStatement stmt = con.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            ArrayList<FoodItem> result = new ArrayList<>();
            while (rs.next()) {
                FoodItem i = new FoodItem();
                i.id = rs.getInt("ID");
                i.eatByDate = rs.getDate("eat_by");
                i.ofType = rs.getInt("of_type");
                i.storedIn = rs.getInt("stored_in");
                i.registers = rs.getInt("registers");
                i.buys = rs.getInt("buys");
                result.add(i);
            }

            return result.toArray(new FoodItem[result.size()]);
        }
    }

    protected String generateTicket() {
        int ticket_length = 64;         // this conforms to database ticket size
        SecureRandom rng = new SecureRandom();
        byte[] content = new byte[ticket_length];

        for (int i = 0; i < ticket_length; i++){
            byte b;
            do {
                b = (byte) rng.nextInt();
            } while (!Character.isLetterOrDigit(b));
            content[i] = b;
        }
        return new String(content);
    }
}
