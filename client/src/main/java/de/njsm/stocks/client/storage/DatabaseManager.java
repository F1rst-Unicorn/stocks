package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.SelectException;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.FoodView;
import de.njsm.stocks.common.data.view.UserDeviceView;
import de.njsm.stocks.common.data.view.UserDeviceViewFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    public DatabaseManager() {
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    private Connection getConnectionWithoutAutoCommit() throws SQLException {
        Connection result = getConnection();
        result.setAutoCommit(false);
        return result;
    }

    public List<Update> getUpdates() throws DatabaseException {
        LOG.info("Getting updates");
        String sql = UpdateFactory.f.getQuery();
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            return UpdateFactory.f.createUpdateList(s.executeQuery());
        } catch (SQLException e) {
            throw new DatabaseException("Could not get updates", e);
        } finally {
            close(c);
        }
    }

    public void writeUpdates(List<Update> u) throws DatabaseException {
        LOG.info("Writing updates");
        String sql = "UPDATE Updates SET last_update=? WHERE table_name=?";
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            PreparedStatement s = c.prepareStatement(sql);

            for (Update item : u) {
                Timestamp t = new Timestamp(item.lastUpdate.getTime());
                s.setTimestamp(1, t);
                s.setString(2, item.table);
                s.execute();
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new DatabaseException("Could not write updates", e);
        } finally {
            close(c);
        }
    }

    public void resetUpdates() throws DatabaseException {
        LOG.info("Resetting updates");
        String sql = "UPDATE Updates SET last_update=0";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            s.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Could not reset updates", e);
        } finally {
            close(c);
        }
    }

    public List<User> getUsers() throws DatabaseException {
        LOG.info("Getting all users");
        String queryUsers = UserFactory.f.getQuery();
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(queryUsers);
            ResultSet rs = p.executeQuery();
            return UserFactory.f.createUserList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get all users", e);
        } finally {
            close(c);
        }
    }

    public List<User> getUsers(String name) throws DatabaseException {
        LOG.info("Getting users matching name '" + name + "'");
        String queryUsers = "SELECT * FROM User WHERE name=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(queryUsers);
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            return UserFactory.f.createUserList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get filtered users", e);
        } finally {
            close(c);
        }
    }

    public void writeUsers(List<User> u) throws DatabaseException {
        LOG.info("Writing users");
        String insertUser = "INSERT INTO User (`ID`, name) VALUES (?,?)";

        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            clearTable(c, "User");
            PreparedStatement insertStmt = c.prepareStatement(insertUser);

            for (User user : u) {
                user.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new DatabaseException("Could not write users", e);
        } finally {
            close(c);
        }
    }

    public void writeDevices(List<UserDevice> u) throws DatabaseException {
        LOG.info("Writing devices");
        String insertDevices = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            clearTable(c, "User_device");
            PreparedStatement insertStmt = c.prepareStatement(insertDevices);

            for (UserDevice dev : u) {
                dev.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new DatabaseException("Could not write devices", e);
        } finally {
            close(c);
        }
    }

    public List<UserDeviceView> getDevices() throws DatabaseException {
        LOG.info("Getting all devices");
        String queryDevices = UserDeviceViewFactory.f.getQuery();
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(queryDevices);
            ResultSet rs = p.executeQuery();
            return UserDeviceViewFactory.f.getViewList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get all devices", e);
        } finally {
            close(c);
        }
    }

    public List<UserDeviceView> getDevices(String name) throws DatabaseException {
        LOG.info("Getting devices for " + name);
        String queryDevices = "SELECT d.id, d.name, u.name as belongs_to " +
                "FROM User_device d, User u " +
                "WHERE d.belongs_to=u.ID AND d.name=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(queryDevices);
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            return UserDeviceViewFactory.f.getViewList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get devices", e);
        } finally {
            close(c);
        }
    }

    public List<Location> getLocations() throws DatabaseException {
        LOG.info("Getting all locations");
        String getLocations = LocationFactory.f.getQuery();
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement selectStmt = c.prepareStatement(getLocations);
            ResultSet rs = selectStmt.executeQuery();
            return LocationFactory.f.createLocationList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get locations", e);
        } finally {
            close(c);
        }
    }

    public List<Location> getLocations(String name) throws DatabaseException {
        LOG.info("Getting locations matching name '" + name + "'");
        String getLocations = "SELECT * FROM Location WHERE name=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement selectStmt = c.prepareStatement(getLocations);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();
            return LocationFactory.f.createLocationList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get locations", e);
        } finally {
            close(c);
        }
    }

    public List<Location> getLocationsForFoodType(int foodId) throws DatabaseException {
        LOG.info("Getting locations for food type " + foodId);
        String getLocations = "SELECT DISTINCT l.ID, l.name " +
                "FROM Location l, Food_item i " +
                "WHERE i.of_type=? AND i.stored_in=l.ID";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement(getLocations);
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            return LocationFactory.f.createLocationList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get locations", e);
        } finally {
            close(c);
        }
    }

    public void writeLocations(List<Location> l) throws DatabaseException {
        LOG.info("Writing locations");
        String insertLocations = "INSERT INTO Location (`ID`, name) VALUES (?,?)";
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            clearTable(c, "Location");
            PreparedStatement insertStmt = c.prepareStatement(insertLocations);

            for (Location loc : l) {
                loc.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new DatabaseException("Could not write locations", e);
        } finally {
            close(c);
        }
    }

    public void writeFood(Food[] f) {
        try {
            Connection c = getConnectionWithoutAutoCommit();
            clearTable(c, "Food");
            String insertFood = "INSERT INTO Food (`ID`, name) VALUES (?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertFood);

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

    public Food[] getFood(String name) {
        try {
            Connection c = getConnection();
            String getFood = "SELECT * FROM Food WHERE name=?";
            PreparedStatement selectStmt = c.prepareStatement(getFood);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();
            ArrayList<Food> result = getFoodResults(rs);
            return result.toArray(new Food[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Food[] getFood() {
        try {
            Connection c = getConnection();
            String getFood = "SELECT * FROM Food";
            PreparedStatement selectStmt = c.prepareStatement(getFood);
            ResultSet rs = selectStmt.executeQuery();
            ArrayList<Food> result = getFoodResults(rs);
            return result.toArray(new Food[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FoodItem[] getItems(int foodId) {
        try {
            Connection c = getConnection();
            FoodItemFactory factory = new FoodItemFactory();

            String queryString = "SELECT * " +
                    "FROM Food_item " +
                    "WHERE of_type=?";

            PreparedStatement sqlQuery = c.prepareStatement(queryString);
            sqlQuery.setInt(1, foodId);
            ResultSet rs = sqlQuery.executeQuery();
            ArrayList<FoodItem> result = new ArrayList<>();
            while (rs.next()) {
                FoodItem i = (FoodItem) factory.createData(rs);
                result.add(i);
            }

            return result.toArray(new FoodItem[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new FoodItem[0];
        }
    }

    public FoodView[] getItems(String user, String location) {
        try {
            Connection c = getConnection();

            String queryString;
            PreparedStatement sqlQuery;

            if (user.equals("")) {
                if (location.equals("")) {
                    queryString = "SELECT f.ID as id, f.name as name, i.eat_by as date " +
                            "FROM Food f LEFT OUTER JOIN Food_item i ON f.ID=i.of_type " +
                            "ORDER BY f.ID ASC, i.eat_by ASC";
                    sqlQuery = c.prepareStatement(queryString);
                } else {
                    queryString = "SELECT f.ID as id, f.name as name, i.eat_by as date " +
                            "FROM Food f LEFT OUTER JOIN Food_item i ON f.ID=i.of_type " +
                            "WHERE i.stored_in in (SELECT ID FROM Location WHERE name=?) " +
                            "ORDER BY f.ID ASC, i.eat_by ASC";
                    sqlQuery = c.prepareStatement(queryString);
                    sqlQuery.setString(1, location);
                }
            } else {
                if (location.equals("")) {
                    queryString = "SELECT f.ID as id, f.name as name, i.eat_by as date " +
                            "FROM Food f LEFT OUTER JOIN Food_item i ON f.ID=i.of_type " +
                            "WHERE i.buys in (SELECT ID FROM User WHERE name=?) " +
                            "ORDER BY f.ID ASC, i.eat_by ASC";
                    sqlQuery = c.prepareStatement(queryString);
                    sqlQuery.setString(1, user);
                } else {
                    queryString = "SELECT f.ID as id, f.name as name, i.eat_by as date " +
                            "FROM Food f LEFT OUTER JOIN Food_item i ON f.ID=i.of_type " +
                            "WHERE i.stored_in in (SELECT ID FROM Location WHERE name=?) " +
                            "AND i.buys in (SELECT ID FROM User WHERE name=?) " +
                            "ORDER BY f.ID ASC, i.eat_by ASC";
                    sqlQuery = c.prepareStatement(queryString);
                    sqlQuery.setString(1, location);
                    sqlQuery.setString(2, user);
                }
            }

            ResultSet rs = sqlQuery.executeQuery();
            ArrayList<FoodView> result = getFoodView(rs);

            return result.toArray(new FoodView[result.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new FoodView[0];
        }
    }

    public void writeFoodItems(FoodItem[] f) {
        try {
            Connection c = getConnectionWithoutAutoCommit();
            clearTable(c, "Food_item");
            String insertItem = "INSERT INTO Food_item " +
                    "(`ID`, eat_by, of_type, stored_in, registers, buys) VALUES (?,?,?,?,?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertItem);

            for (FoodItem food : f) {
                food.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            c.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNextItem(int foodId) throws SelectException {
        try {
            Connection c = getConnection();

            String query = "SELECT * " +
                    "FROM Food_item " +
                    "WHERE of_type=? " +
                    "ORDER BY eat_by ASC " +
                    "LIMIT 1";

            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int result = rs.getInt("ID");
                rs.close();
                return result;
            } else {
                rs.close();
                throw new SelectException("You don't have any...");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void writeAll(User[] users,
                         UserDevice[] devices,
                         Location[] locations,
                         Food[] foods,
                         FoodItem[] items) {
        try {
            Connection c = getConnectionWithoutAutoCommit();

            String insertUser = "INSERT INTO User (`ID`, name) VALUES (?,?)";
            String insertDevice = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";
            String insertLocation = "INSERT INTO Location (`ID`, name) VALUES (?,?)";
            String insertFood = "INSERT INTO User (`ID`, name) VALUES (?,?)";
            String insertItem = "INSERT INTO Food_item (`ID`, eat_by, of_type, stored_in, registers, buys) VALUES (?,?,?,?,?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertUser);

            clearTable(c, "User");
            clearTable(c, "User_device");
            clearTable(c, "Location");
            clearTable(c, "Food");
            clearTable(c, "FoodItem");

            for (User user : users) {
                user.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertDevice);
            for (UserDevice dev : devices) {
                dev.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertLocation);
            for (Location loc : locations) {
                loc.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertFood);
            for (Food food : foods) {
                food.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertItem);
            for (FoodItem food : items) {
                food.fillAddStmtWithId(insertStmt);
                insertStmt.execute();
            }

            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<FoodView> getFoodView(ResultSet rs) throws SQLException {
        ArrayList<FoodView> result = new ArrayList<>();
        int lastId = -1;
        FoodView f = null;
        while (rs.next()) {
            int id = rs.getInt("id");

            if (id != lastId) {
                if (f != null) {
                    result.add(f);
                }
                Food newFood = new Food();
                newFood.id = id;
                newFood.name = rs.getString("name");
                f = new FoodView(newFood);
            }
            FoodItem i = new FoodItem();
            i.ofType = id;
            i.eatByDate = rs.getDate("date");
            if (i.eatByDate != null) {
                f.add(i);
            }
            lastId = id;
        }
        if (f != null) {
            result.add(f);
        }
        return result;
    }

    private ArrayList<Location> getLocationResults(ResultSet rs) throws SQLException {
        ArrayList<Location> result = new ArrayList<>();
        while (rs.next()) {
            Location l = new Location();
            l.name = rs.getString("name");
            l.id = rs.getInt("ID");
            result.add(l);
        }
        return result;
    }

    private ArrayList<Food> getFoodResults(ResultSet rs) throws SQLException {
        ArrayList<Food> result = new ArrayList<>();
        while (rs.next()) {
            Food f = new Food();
            f.name = rs.getString("name");
            f.id = rs.getInt("ID");
            result.add(f);
        }
        return result;
    }

    private static void clearTable(Connection c, String name) throws SQLException {
        LOG.info("Clearing table " + name);
        String sqlString = "DELETE FROM " + name;
        PreparedStatement s = c.prepareStatement(sqlString);
        s.execute();
    }

    static void rollback(Connection c) {
        if (c != null) {
            try {
                LOG.warn("Rolling back transaction");
                c.rollback();
                LOG.info("Successful rollback");
            } catch (SQLException e) {
                LOG.error("Rollback failed", e);
            }
        }
    }

    static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }
}
