package de.njsm.stocks.linux.client.storage;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Update;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.data.UserDevice;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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

    public void writeDevices(User[] u) {
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
            String deleteUsers = "DELETE FROM User_device";
            String insertUser = "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";

            PreparedStatement deleteStmt = c.prepareStatement(deleteUsers);
            PreparedStatement insertStmt = c.prepareStatement(insertUser);

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

}
