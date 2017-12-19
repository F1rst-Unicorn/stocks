package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.visitor.AddStatementVisitor;
import de.njsm.stocks.common.data.visitor.SqlStatementFillerVisitor;
import de.njsm.stocks.server.internal.auth.AuthAdmin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDatabaseHandler implements DatabaseHandler{

    private static final Logger LOG = LogManager.getLogger(SqlDatabaseHandler.class);

    private AddStatementVisitor addStatementVisitor;

    private SqlStatementFillerVisitor fillerVisitor;

    private final String url;
    private final AuthAdmin authAdmin;
    private final String username;
    private final String password;

    public SqlDatabaseHandler(String url,
                              String username,
                              String password,
                              AuthAdmin authAdmin) {

        this.authAdmin = authAdmin;
        this.addStatementVisitor = new AddStatementVisitor();
        this.fillerVisitor = new SqlStatementFillerVisitor();
        this.username = username;
        this.password = password;
        this.url = url;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void add(Data d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(addStatementVisitor.visit(d, null));
            fillerVisitor.visit(d, stmt);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to add " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void rename(SqlRenamable d, String newName) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRenameStmt());
            d.fillRenameStmt(stmt, newName);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to rename " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void remove(SqlRemovable d) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRemoveStmt());
            d.fillRemoveStmt(stmt);
            stmt.execute();
        } catch (SQLException e) {
            LOG.error("Failed to remove " + d.toString(), e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public void removeUser(User u){

        String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
        String deleteDevicesCommand = "DELETE FROM User_device WHERE belongs_to=?";
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

            certificateList.forEach(authAdmin::revokeCertificate);
        } catch (SQLException e){
            LOG.error("Error deleting devices", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public Ticket addDevice(UserDevice d) {

        String addTicket = "INSERT INTO Ticket (ticket, belongs_device, created_on) VALUES (?,LAST_INSERT_ID(), NOW())";
        Ticket result = new Ticket();
        result.ticket = Ticket.generateTicket();
        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlAddDevice = con.prepareStatement(addStatementVisitor.visit(d), Statement.RETURN_GENERATED_KEYS);
            PreparedStatement sqlAddTicket = con.prepareStatement(addTicket);

            con.setAutoCommit(false);
            fillerVisitor.visit(d, sqlAddDevice);
            sqlAddDevice.execute();
            ResultSet keys = sqlAddDevice.getGeneratedKeys();
            keys.next();
            result.deviceId = keys.getInt(1);

            sqlAddTicket.setString(1, result.ticket);
            sqlAddTicket.execute();


            con.commit();

        } catch (SQLException e) {
            LOG.error("Error adding device", e);
            rollback(con);
            result.ticket = null;
        } finally {
            close(con);
        }
        return result;
    }

    @Override
    public void removeDevice(UserDevice u){

        Connection con = null;

        try {
            con = getConnection();
            PreparedStatement sqlStmt=con.prepareStatement(u.getRemoveStmt());
            con.setAutoCommit(false);
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();

            authAdmin.revokeCertificate(u.id);

        } catch (SQLException e){
            LOG.error("Error deleting devices", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    @Override
    public Data[] get(DataFactory df) {
        Connection con = null;
        try {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(df.getQuery());
            ResultSet rs = stmt.executeQuery();
            List<Data> result = df.createDataList(rs);
            return result.toArray(new Data[result.size()]);
        } catch (SQLException e) {
            LOG.error("Error getting data", e);
            rollback(con);
        } finally {
            close(con);
        }
        return new Data[0];
    }

    @Override
    public void moveItem(FoodItem item, int loc) {
        Connection con = null;
        String sqlString = "UPDATE Food_item SET stored_in=? WHERE ID=?";

        try {

            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sqlString);
            stmt.setInt(1, loc);
            stmt.setInt(2, item.id);
            stmt.executeQuery();

        } catch (SQLException e) {
            LOG.error("Error moving item", e);
            rollback(con);
        } finally {
            close(con);
        }
    }

    protected void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    protected void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOG.error("Error while rollback", e1);
            }
        }
    }

}
