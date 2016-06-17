package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.CertificateAdmin;
import de.njsm.stocks.server.internal.auth.Principals;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SqlDatabaseHandler {

    protected final String url;
    protected final Config c;

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

    public void add(SqlAddable d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getAddStmt());
            d.fillAddStmt(stmt);
            stmt.execute();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Database: Failed to add " + d.toString() + ": " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Database: Failed to rollback: " + e1.getMessage());
                }
            }
        }
    }

    public void rename(SqlRenamable d, String newName) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRenameStmt());
            d.fillRenameStmt(stmt, newName);
            stmt.execute();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Database: Failed to rename " + d.toString() + ": " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Database: Failed to rollback: " + e1.getMessage());
                }
            }
        }
    }

    public void remove(SqlRemovable d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRemoveStmt());
            d.fillRemoveStmt(stmt);
            stmt.execute();
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Database: Failed to remove " + d.toString() + ": " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Database: Failed to rollback: " + e1.getMessage());
                }
            }
        }
    }

    public void removeUser(User u){

        String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
        String deleteDevicesCommand = "DELETE FROM User_device WHERE belongs_to=?";
        CertificateAdmin ca = new Config().getCertAdmin();
        List<Integer> certificateList = new ArrayList<>();
        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
            PreparedStatement sqlStmt=con.prepareStatement(u.getRemoveStmt());
            PreparedStatement sqlDeleteDevices = con.prepareStatement(deleteDevicesCommand);

            con.setAutoCommit(false);
            // revoke all devices
            sqlQuery.setInt(1, u.id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()){
                certificateList.add(res.getInt("ID"));
            }

            sqlDeleteDevices.setInt(1, u.id);
            sqlDeleteDevices.execute();
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();

            con.commit();

            certificateList.forEach(ca::revokeCertificate);
        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "Error deleting devices: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Error while rollback: " + e1.getMessage());
                }
            }
        }

    }

    public Ticket addDevice(UserDevice d) {

        String addTicket = "INSERT INTO Ticket (ticket, belongs_device, created_on) VALUES (?,LAST_INSERT_ID(), NOW())";
        Ticket result = new Ticket();
        result.ticket = Ticket.generateTicket();
        result.deviceId = d.id;
        Connection con = null;

        try {

            con = getConnection();
            PreparedStatement sqlAddDevice = con.prepareStatement(d.getAddStmt());
            PreparedStatement sqlAddTicket = con.prepareStatement(addTicket);

            con.setAutoCommit(false);
            d.fillAddStmt(sqlAddDevice);
            sqlAddDevice.execute();

            sqlAddTicket.setString(1, result.ticket);
            sqlAddTicket.execute();
            con.commit();

        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Error adding device: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Error while rollback: " + e1.getMessage());
                }
            }
            result.ticket = null;
        }
        return result;
    }

    public void removeDevice(UserDevice u){

        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlStmt=con.prepareStatement(u.getRemoveStmt());
            con.setAutoCommit(false);
            u.fillAddStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();

            c.getCertAdmin().revokeCertificate(u.id);

        } catch (SQLException e){
            c.getLog().log(Level.SEVERE, "Error deleting devices: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Error while rollback: " + e1.getMessage());
                }
            }
        }
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
                d.lastUpdate = rs.getTimestamp("last_poll");
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

    public Update[] getUpdates() throws SQLException {
        String query = "SELECT * FROM Updates";

        try (Connection con = getConnection();
             PreparedStatement sqlQuery = con.prepareStatement(query)){
            ResultSet res = sqlQuery.executeQuery();
            ArrayList<Update> result = new ArrayList<>();
            while (res.next()) {
                Update u = new Update();
                u.table = res.getString("table_name");
                u.lastUpdate = res.getTimestamp("last_update");
                result.add(u);
            }

            return result.toArray(new Update[result.size()]);
        }
    }

}
