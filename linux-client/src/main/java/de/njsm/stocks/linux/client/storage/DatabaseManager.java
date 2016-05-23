package de.njsm.stocks.linux.client.storage;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.*;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    public DatabaseManager() {

    }

    protected Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + Configuration.dbPath);
        return c;
    }

    public Update[] getUpdates() {
        try {
            Connection c = getConnection();
            String sql = "SELECT * FROM Updates";
            PreparedStatement s = c.prepareStatement(sql);

            ArrayList<Update> result = new ArrayList<>(5);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Update u = new Update();
                u.table = rs.getString("table_name");
                u.lastUpdate = rs.getTimestamp("last_update");
                result.add(u);
            }

            return result.toArray(new Update[result.size()]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void writeUpdates(Update[] u) {
        try {
            Connection c = getConnection();
            String sql = "UPDATE Updates SET last_update=? WHERE table_name=?";
            PreparedStatement s = c.prepareStatement(sql);

            for (Update item : u) {
                Timestamp t = new Timestamp(item.lastUpdate.getTime());
                s.setTimestamp(1, t);
                s.setString(2, item.table);
                s.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User[] getUsers() {
        try {
            Connection c = getConnection();
            String queryUsers = "SELECT * FROM User";

            PreparedStatement p = c.prepareStatement(queryUsers);

            ResultSet rs = p.executeQuery();
            ArrayList<User> result = getUserResult(rs);
            return result.toArray(new User[result.size()]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User[] getUsers(String name) {
        try {
            Connection c = getConnection();
            String queryUsers = "SELECT * FROM User WHERE name=?";

            PreparedStatement p = c.prepareStatement(queryUsers);
            p.setString(1, name);


            ResultSet rs = p.executeQuery();
            ArrayList<User> result = getUserResult(rs);
            return result.toArray(new User[result.size()]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeUsers(User[] u) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            String deleteUsers = "DELETE FROM User";
            String insertUser = "INSERT INTO User (`ID`, name) VALUES (?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteUsers);
            PreparedStatement insertStmt = c.prepareStatement(insertUser);

            deleteStmt.execute();

            for (User user : u) {
                insertStmt.setInt(1, user.id);
                insertStmt.setString(2, user.name);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeDevices(UserDevice[] u) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            String deleteDevices = "DELETE FROM User_device";
            String insertDevices = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteDevices);
            PreparedStatement insertStmt = c.prepareStatement(insertDevices);

            deleteStmt.execute();

            for (UserDevice dev : u) {
                insertStmt.setInt(1, dev.id);
                insertStmt.setString(2, dev.name);
                insertStmt.setInt(3, dev.userId);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location[] getLocations() {
        try {
            Connection c = getConnection();
            String getLocations = "SELECT * FROM Location";
            PreparedStatement selectStmt = c.prepareStatement(getLocations);
            ResultSet rs = selectStmt.executeQuery();
            ArrayList<Location> result = getLocationResults(rs);
            return result.toArray(new Location[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Location[] getLocations(String name) {
        try {
            Connection c = getConnection();
            String getLocations = "SELECT * FROM Location WHERE name=?";
            PreparedStatement selectStmt = c.prepareStatement(getLocations);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();
            ArrayList<Location> result = getLocationResults(rs);
            return result.toArray(new Location[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeLocations(Location[] l) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            String deleteLocations = "DELETE FROM Location";
            String insertLocations = "INSERT INTO Location (`ID`, name) VALUES (?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteLocations);
            PreparedStatement insertStmt = c.prepareStatement(insertLocations);

            deleteStmt.execute();

            for (Location loc : l) {
                insertStmt.setInt(1, loc.id);
                insertStmt.setString(2, loc.name);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeFood(Food[] f) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            String deleteFood = "DELETE FROM Food";
            String insertFood = "INSERT INTO Food (`ID`, name) VALUES (?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteFood);
            PreparedStatement insertStmt = c.prepareStatement(insertFood);

            deleteStmt.execute();

            for (Food food : f) {
                insertStmt.setInt(1, food.id);
                insertStmt.setString(2, food.name);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeFoodItems(FoodItem[] f) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            String deleteFood = "DELETE FROM Food_item";
            String insertFood = "INSERT INTO Food_item " +
                    "(`ID`, of_type, stored_in, registers, buys, eat_by) VALUES (?,?,?,?,?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteFood);
            PreparedStatement insertStmt = c.prepareStatement(insertFood);

            deleteStmt.execute();

            for (FoodItem food : f) {
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(food.eatByDate.getTime());
                insertStmt.setInt(1, food.id);
                insertStmt.setInt(2, food.ofType);
                insertStmt.setInt(3, food.storedIn);
                insertStmt.setInt(4, food.registers);
                insertStmt.setInt(5, food.buys);
                insertStmt.setTimestamp(6, sqlDate);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<User> getUserResult(ResultSet rs) throws SQLException {
        ArrayList<User> result = new ArrayList<>();
        while (rs.next()) {
            User u = new User();
            u.name = rs.getString("name");
            u.id = rs.getInt("ID");
            result.add(u);
        }
        return result;
    }

    protected ArrayList<Location> getLocationResults(ResultSet rs) throws SQLException {
        ArrayList<Location> result = new ArrayList<>();
        while (rs.next()) {
            Location l = new Location();
            l.name = rs.getString("name");
            l.id = rs.getInt("ID");
            result.add(l);
        }
        return result;
    }
}
