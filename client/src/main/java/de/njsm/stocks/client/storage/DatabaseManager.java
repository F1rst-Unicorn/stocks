package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.*;
import de.njsm.stocks.client.data.view.FoodView;
import de.njsm.stocks.client.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.SelectException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    public DatabaseManager() {
        LOG.info("DB is at " + Configuration.DB_PATH);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    public List<Update> getUpdates() throws DatabaseException {
        LOG.info("Getting updates");
        try (Connection c = getConnection()) {
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

            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Could not get updates", e);
        }
    }


    public void writeUpdates(List<Update> u) throws DatabaseException {
        LOG.info("Writing updates");
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            String sql = "UPDATE Updates SET last_update=? WHERE table_name=?";
            PreparedStatement s = c.prepareStatement(sql);

            for (Update item : u) {
                Timestamp t = new Timestamp(item.lastUpdate.getTime());
                s.setTimestamp(1, t);
                s.setString(2, item.table);
                s.execute();
            }
            c.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Could not write updates", e);
        }
    }

    public void resetUpdates() throws DatabaseException {
        LOG.info("Resetting updates");
        try (Connection c = getConnection()) {
            String sql = "UPDATE Updates SET last_update=0";
            PreparedStatement s = c.prepareStatement(sql);
            s.execute();

        } catch (SQLException e) {
            throw new DatabaseException("Could not reset updates", e);
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

            (new DatabaseOperator(c)).clearTable("User");
            String insertUser = "INSERT INTO User (`ID`, name) VALUES (?,?)";
            PreparedStatement insertStmt = c.prepareStatement(insertUser);

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

            (new DatabaseOperator(c)).clearTable("User_device");
            String insertDevices = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertDevices);

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

    public UserDeviceView[] getDevices() {
        try {
            Connection c = getConnection();
            String queryDevices = "SELECT d.id, d.name, u.name as belongs_to " +
                    "FROM User_device d, User u " +
                    "WHERE d.belongs_to=u.ID";

            PreparedStatement p = c.prepareStatement(queryDevices);

            ResultSet rs = p.executeQuery();
            ArrayList<UserDeviceView> result = getDeviceResults(rs);
            return result.toArray(new UserDeviceView[result.size()]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserDeviceView[] getDevices(String name) {
        try {
            Connection c = getConnection();
            String queryDevices = "SELECT * FROM User_device WHERE name=?";

            PreparedStatement p = c.prepareStatement(queryDevices);
            p.setString(1, name);

            ResultSet rs = p.executeQuery();
            ArrayList<UserDeviceView> result = getDeviceResults(rs);
            return result.toArray(new UserDeviceView[result.size()]);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public Location[] getLocationsForFoodType(int foodId) {
        try {
            Connection c = getConnection();
            String getLocations = "SELECT DISTINCT l.ID, l.name " +
                    "FROM Location l, Food_item i " +
                    "WHERE i.of_type=? AND i.stored_in=l.ID";
            PreparedStatement stmt = c.prepareStatement(getLocations);
            stmt.setInt(1, foodId);
            ArrayList<Location> result = getLocationResults(stmt.executeQuery());
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
            (new DatabaseOperator(c)).clearTable("Location");
            String insertLocations = "INSERT INTO Location (`ID`, name) VALUES (?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertLocations);

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
            (new DatabaseOperator(c)).clearTable("Food");
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
            Connection c = getConnection();
            c.setAutoCommit(false);
            (new DatabaseOperator(c)).clearTable("Food_item");
            String insertFood = "INSERT INTO Food_item " +
                    "(`ID`, of_type, stored_in, registers, buys, eat_by) VALUES (?,?,?,?,?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertFood);

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
            Connection c = getConnection();
            c.setAutoCommit(false);
            DatabaseOperator op = new DatabaseOperator(c);

            String insertUser = "INSERT INTO User (`ID`, name) VALUES (?,?)";
            String insertDevice = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";
            String insertLocation = "INSERT INTO Location (`ID`, name) VALUES (?,?)";
            String insertFood = "INSERT INTO User (`ID`, name) VALUES (?,?)";
            String insertItem = "INSERT INTO Food_item " +
                    "(`ID`, of_type, stored_in, registers, buys, eat_by) VALUES (?,?,?,?,?,?)";

            PreparedStatement insertStmt = c.prepareStatement(insertUser);

            op.clearTable("User");
            op.clearTable("User_device");
            op.clearTable("Location");
            op.clearTable("Food");
            op.clearTable("FoodItem");

            for (User user : users) {
                insertStmt.setInt(1, user.id);
                insertStmt.setString(2, user.name);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertDevice);
            for (UserDevice dev : devices) {
                insertStmt.setInt(1, dev.id);
                insertStmt.setString(2, dev.name);
                insertStmt.setInt(3, dev.userId);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertLocation);
            for (Location loc : locations) {
                insertStmt.setInt(1, loc.id);
                insertStmt.setString(2, loc.name);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertFood);
            for (Food food : foods) {
                insertStmt.setInt(1, food.id);
                insertStmt.setString(2, food.name);
                insertStmt.execute();
            }

            insertStmt = c.prepareStatement(insertItem);
            for (FoodItem food : items) {
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

    protected ArrayList<FoodView> getFoodView(ResultSet rs) throws SQLException {
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

    protected ArrayList<Food> getFoodResults(ResultSet rs) throws SQLException {
        ArrayList<Food> result = new ArrayList<>();
        while (rs.next()) {
            Food f = new Food();
            f.name = rs.getString("name");
            f.id = rs.getInt("ID");
            result.add(f);
        }
        return result;
    }

    protected ArrayList<UserDeviceView> getDeviceResults(ResultSet rs) throws SQLException {
        ArrayList<UserDeviceView> result = new ArrayList<>();
        while (rs.next()) {
            UserDeviceView d = new UserDeviceView();
            d.name = rs.getString("name");
            d.id = rs.getInt("ID");
            d.user = rs.getString("belongs_to");
            result.add(d);
        }
        return result;
    }


}
