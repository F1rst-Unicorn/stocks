package de.njsm.stocks.internal.db;

import de.njsm.stocks.data.*;
import de.njsm.stocks.internal.auth.UserContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlDatabaseHandler implements DatabaseHandler {

    private static DataSource dataSource;
    static {
        try {
            Context serverContext = (Context) new InitialContext().lookup("java:/comp/env");
            dataSource = (DataSource) serverContext.lookup("jdbc/stocks-dev");
        } catch (NamingException e) {
            throw new Error("Could not find database", e);
        }
    }

    public void addLocation(Location location) throws SQLException {

    }

    public void removeLocation(int id) throws SQLException {

    }

    public void renameLocation(int id, String new_name) throws SQLException {

    }

    public void removeUser(int id) throws SQLException {

    }

    public void removeDevice(int id) throws SQLException {

    }

    public void addFood(UserContext c, Food food) throws SQLException {

    }

    public void removeFood(UserContext c, int id) throws SQLException {

    }

    public void renameFood(UserContext c, int id, String new_name) throws SQLException {

    }

    public void addFoodItem(UserContext c, FoodItem item) throws SQLException {

    }

    public void removeFoodItem(UserContext c, int id) throws SQLException {

    }

    public String getNewTicket() throws SQLException {
        return null;
    }

    public Location[] getLocations() throws SQLException {

        String query = "SELECT * " +
                       "FROM LOCATION";
        try (
                Connection con = dataSource.getConnection();
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
                Connection con = dataSource.getConnection();
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
                Connection con = dataSource.getConnection();
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
                Connection con = dataSource.getConnection();
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
                Connection con = dataSource.getConnection();
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
}
