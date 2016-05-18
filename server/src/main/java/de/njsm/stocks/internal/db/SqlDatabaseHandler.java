package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.auth.CertificateAdmin;
import de.njsm.stocks.internal.auth.Principals;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void addUser(User u) throws SQLException {
        String command = "INSERT INTO User (name) VALUES (?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt = con.prepareStatement(command)) {

            sqlStmt.setString(1, u.name);
            sqlStmt.execute();
        }
    }

    public void removeUser(int id) throws SQLException {

        String command="DELETE FROM User WHERE ID=?";
        String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
        String deleteDevicesCommand = "DELETE FROM User_device WHERE belongs_to=?";
        CertificateAdmin ca = new Config().getCertAdmin();
        List<Integer> certificateList = new ArrayList<>();
        Connection con = null;

        try {

            con = getConnection();
            PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
            PreparedStatement sqlStmt=con.prepareStatement(command);
            PreparedStatement sqlDeleteDevices = con.prepareStatement(deleteDevicesCommand);

            con.setAutoCommit(false);
            // revoke all devices
            sqlQuery.setInt(1, id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()){
                certificateList.add(res.getInt("certificate_no"));
            }

            sqlDeleteDevices.setInt(1, id);
            sqlDeleteDevices.execute();
            sqlStmt.setInt(1, id);
            sqlStmt.execute();

            con.commit();

            for (Integer i : certificateList){
                ca.revokeCertificate(i);
            }
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "Error deleting devices: " + e.getMessage());
            if (con != null) {
                con.rollback();
            }
        }

    }

    @Override
    public String addDevice(UserDevice d) throws SQLException {

        String addDevice = "INSERT INTO User_device (name, belongs_to) VALUES (?,?)";
        String addTicket = "INSERT INTO Ticket (ticket, belongs_device) VALUES (?,LAST_INSERT_ID())";
        String ticket = generateTicket();
        Connection con = null;

        try {

            con = getConnection();
            PreparedStatement sqlAddDevice = con.prepareStatement(addDevice);
            PreparedStatement sqlAddTicket = con.prepareStatement(addTicket);

            con.setAutoCommit(false);
            sqlAddDevice.setString(1, d.name);
            sqlAddDevice.setInt(2, d.userId);
            sqlAddDevice.execute();

            sqlAddTicket.setString(1, ticket);
            sqlAddDevice.execute();
            con.commit();

        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Error adding device: " + e.getMessage());
            if (con != null) {
                con.rollback();
            }
        }
        return ticket;
    }

    public void removeDevice(int id) throws SQLException {


        String query = "SELECT certificate_no FROM User_device WHERE ID=?";
        String command="DELETE FROM User_device WHERE ID=?";
        Connection con = null;
        int certId = -1;

        try {

            con = getConnection();
            PreparedStatement sqlQuery = con.prepareStatement(query);
            PreparedStatement sqlStmt=con.prepareStatement(command);

            con.setAutoCommit(false);
            // revoke device
            sqlQuery.setInt(1, id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()){
                certId = res.getInt("certificate_no");
            }

            sqlStmt.setInt(1, id);
            sqlStmt.execute();

            con.commit();

            c.getCertAdmin().revokeCertificate(certId);

        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "Error deleting devices: " + e.getMessage());
            if (con != null) {
                con.rollback();
            }
        }
    }

    public void addFood(Principals c, Food food) throws SQLException {
        String command="INSERT INTO Food (name) VALUES (?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setString(1, food.name);
            sqlStmt.execute();
        }
    }

    public void removeFood(Principals c, int id) throws SQLException {
        String command="DELETE FROM Food WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setInt(1, id);
            sqlStmt.execute();
        }
    }

    public void renameFood(Principals c, int id, String new_name) throws SQLException {
        String command="UPDATE Food SET name=? WHERE ID=?";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setString(1, new_name);
            sqlStmt.setInt(2, id);
            sqlStmt.execute();
        }
    }

    public void addFoodItem(Principals c, FoodItem item) throws SQLException {
        String command="INSERT INTO Food_item (eat_by, of_type, stored_in, registers, buys) " +
                "VALUES (?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement sqlStmt=con.prepareStatement(command)) {

            sqlStmt.setDate(1, new java.sql.Date(item.eatByDate.getTime()));
            sqlStmt.setInt(2, item.ofType);
            sqlStmt.setInt(3, item.storedIn);
            sqlStmt.setInt(4, c.getDid());
            sqlStmt.setInt(5, c.getUid());
            sqlStmt.execute();
        }
    }

    public void removeFoodItem(Principals c, int id) throws SQLException {
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

    @Override
    public Update[] getUpdates() throws SQLException {
        String query = "SELECT * FROM Updates";

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query)){
            ResultSet res = sqlQuery.executeQuery();
            ArrayList<Update> result = new ArrayList<>();
            while (res.next()) {
                Update u = new Update();
                u.table = res.getString("table_name");
                u.lastUpdate = res.getDate("last_update");
                result.add(u);
            }

            return result.toArray(new Update[result.size()]);
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
