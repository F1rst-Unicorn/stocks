package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import de.njsm.stocks.client.exceptions.InputException;
import de.njsm.stocks.client.init.upgrade.Version;
import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.FoodView;
import de.njsm.stocks.common.data.view.UserDeviceView;
import de.njsm.stocks.common.data.view.UserDeviceViewFactory;
import de.njsm.stocks.common.data.visitor.VisitorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    private DatabaseImpl backend;

    public DatabaseManager() {
        backend = new DatabaseImpl();
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
        return getData(UpdateFactory.f);
    }

    public void writeUpdates(List<Update> u) throws DatabaseException {
        writeData("Updates", u);
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
        return getData(UserFactory.f);
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
            return UserFactory.f.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get filtered users", e);
        } finally {
            close(c);
        }
    }

    public void writeUsers(List<User> u) throws DatabaseException {
        writeData("User", u);
    }

    public void writeDevices(List<UserDevice> u) throws DatabaseException {
        writeData("User_device", u);
    }

    public List<UserDeviceView> getDevices() throws DatabaseException {
        LOG.info("Getting all devices");
        return getData(UserDeviceViewFactory.f);
    }

    public List<UserDeviceView> getDevices(String name) throws DatabaseException {
        LOG.info("Getting devices for " + name);
        String queryDevices = "SELECT d.id, d.name, u.name as belongs_to, u.ID as belongs_id " +
                "FROM User_device d, User u " +
                "WHERE d.belongs_to=u.ID AND d.name=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(queryDevices);
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            return UserDeviceViewFactory.f.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get devices", e);
        } finally {
            close(c);
        }
    }

    public List<Location> getLocations() throws DatabaseException {
        LOG.info("Getting all locations");
        return getData(LocationFactory.f);
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
            return LocationFactory.f.createDataList(rs);
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
            return LocationFactory.f.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get locations", e);
        } finally {
            close(c);
        }
    }

    public void writeLocations(List<Location> l) throws DatabaseException {
        writeData("Location", l);
    }

    public void writeFood(List<Food> f) throws DatabaseException {
        writeData("Food", f);
    }

    public List<Food> getFood(String name) throws DatabaseException {
        LOG.info("Getting food matching name '" + name + "'");
        String getFood = "SELECT * FROM Food WHERE name=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement selectStmt = c.prepareStatement(getFood);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();
            return FoodFactory.f.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get food", e);
        } finally {
            close(c);
        }
    }

    public List<Food> getFood() throws DatabaseException {
        LOG.info("Getting all food");
        return getData(FoodFactory.f);
    }

    public List<FoodItem> getItems(int foodId) throws DatabaseException {
        LOG.info("Getting food items of type " + foodId);
        String queryString = "SELECT * " +
                "FROM Food_item " +
                "WHERE of_type=?";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement sqlQuery = c.prepareStatement(queryString);
            sqlQuery.setInt(1, foodId);
            ResultSet rs = sqlQuery.executeQuery();
            return FoodItemFactory.f.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get food items", e);
        } finally {
            close(c);
        }
    }

    public List<FoodView> getItems(String user, String location) throws DatabaseException {
        LOG.info("Getting items matching user '" + user + "' and location '" + location + "'");
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement sqlQuery = getStatementFilteringBy(user, location, c);
            ResultSet rs = sqlQuery.executeQuery();
            return getFoodView(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get items", e);
        } finally {
            close(c);
        }
    }

    private PreparedStatement getStatementFilteringBy(String user, String location, Connection c) throws SQLException {
        String queryStringTemplate = "SELECT f.ID as id, f.name as name, i.eat_by as date " +
                "FROM Food f LEFT OUTER JOIN Food_item i ON f.ID=i.of_type %s" +
                "ORDER BY f.ID ASC, i.eat_by ASC";
        String queryString;
        PreparedStatement sqlQuery;
        if (user.equals("")) {
            if (location.equals("")) {
                String filterClause = "";
                queryString = String.format(queryStringTemplate, filterClause);
                sqlQuery = c.prepareStatement(queryString);
            } else {
                String filterClause = "WHERE i.stored_in in (SELECT ID FROM Location WHERE name=?)";
                queryString = String.format(queryStringTemplate, filterClause);
                sqlQuery = c.prepareStatement(queryString);
                sqlQuery.setString(1, location);
            }
        } else {
            if (location.equals("")) {
                String filterClause = "WHERE i.buys in (SELECT ID FROM User WHERE name=?)";
                queryString = String.format(queryStringTemplate, filterClause);
                sqlQuery = c.prepareStatement(queryString);
                sqlQuery.setString(1, user);
            } else {
                String filterClause = "WHERE i.stored_in in (SELECT ID FROM Location WHERE name=?) " +
                        "AND i.buys in (SELECT ID FROM User WHERE name=?)";
                queryString = String.format(queryStringTemplate, filterClause);
                sqlQuery = c.prepareStatement(queryString);
                sqlQuery.setString(1, location);
                sqlQuery.setString(2, user);
            }
        }
        return sqlQuery;
    }

    public void writeFoodItems(List<FoodItem> list) throws DatabaseException {
        writeData("Food_item", list);
    }

    public FoodItem getNextItem(int foodId) throws InputException, DatabaseException {
        LOG.info("Getting next item for food id " + foodId);
        String query = "SELECT * " +
                "FROM Food_item " +
                "WHERE of_type=? " +
                "ORDER BY eat_by ASC " +
                "LIMIT 1";
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return FoodItemFactory.f.createData(rs);
            } else {
                throw new InputException("You don't have any...");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get next food item", e);
        } finally {
            close(c);
        }
    }

    public Version getDbVersion() throws DatabaseException {
        LOG.info("Getting version");
        String tableQuery = "SELECT name FROM sqlite_master WHERE type='table' " +
                "AND name='Config' ";
        String query = "SELECT value FROM Config WHERE key='db.version'";
        Connection c = null;

        try {
            c = getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(tableQuery);
            if (rs.next()) {
                ResultSet configResult = stmt.executeQuery(query);
                return Version.create(configResult.getString("value"));
            } else {
                return Version.PRE_VERSIONED;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get DB version", e);
        } finally {
            close(c);
        }
    }

    public void runSqlScript(List<String> script) throws DatabaseException {
        LOG.info("Running script");
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            Statement stmt = c.createStatement();
            for (String command : script) {
                stmt.execute(command);
            }
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Could complete SQL script", e);
        } finally {
            close(c);
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
            Timestamp date = rs.getTimestamp("date");
            if (date != null && f != null) {
                f.add(new Date(date.getTime()));
            }
            lastId = id;
        }
        if (f != null) {
            result.add(f);
        }
        return result;
    }

    private void writeData(String table, List<? extends Data> list) throws DatabaseException {
        LOG.info("Writing " + table);
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            backend.writeData(c, table, list);
            c.commit();
        } catch (SQLException | VisitorException e) {
            rollback(c);
            throw new DatabaseException("Could not write " + table, e);
        } finally {
            close(c);
        }
    }

    private <T extends Data> List<T> getData(DataFactory<T> factory) throws DatabaseException {
        String query = factory.getQuery();
        Connection c = null;

        try {
            c = getConnection();
            PreparedStatement p = c.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            return factory.createDataList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get all data", e);
        } finally {
            close(c);
        }
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
