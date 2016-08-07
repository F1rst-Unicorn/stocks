package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.X509CertificateAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SqlDatabaseHandler {

    private final String url;
    private final Config c;

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
        X509CertificateAdmin ca = c.getCertAdmin();
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
        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlAddDevice = con.prepareStatement(d.getAddStmt(), Statement.RETURN_GENERATED_KEYS);
            PreparedStatement sqlAddTicket = con.prepareStatement(addTicket);

            con.setAutoCommit(false);
            d.fillAddStmt(sqlAddDevice);
            sqlAddDevice.execute();
            ResultSet keys = sqlAddDevice.getGeneratedKeys();
            keys.next();
            result.deviceId = keys.getInt(1);

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
            u.fillRemoveStmt(sqlStmt);
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

    public Data[] get(DataFactory df) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(df.getQuery());
            ResultSet rs = stmt.executeQuery();
            List<Data> result = df.createDataList(rs);
            return result.toArray(new Data[result.size()]);
        } catch (SQLException e) {
            c.getLog().log(Level.SEVERE, "Error getting data: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    c.getLog().log(Level.SEVERE, "Error while rollback: " + e1.getMessage());
                }
            }
        }
        return new Data[0];
    }

}
