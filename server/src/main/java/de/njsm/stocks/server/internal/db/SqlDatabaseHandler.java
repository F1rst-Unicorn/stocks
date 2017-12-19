package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.visitor.AddStatementVisitor;
import de.njsm.stocks.common.data.visitor.SqlStatementFillerVisitor;
import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
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
        runSqlOperation(con -> {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(addStatementVisitor.visit(d, null));
            fillerVisitor.visit(d, stmt);
            stmt.execute();
        });
    }

    @Override
    public void rename(SqlRenamable d, String newName) {
        runSqlOperation(con -> {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRenameStmt());
            d.fillRenameStmt(stmt, newName);
            stmt.execute();
        });
    }

    @Override
    public void remove(SqlRemovable d) {
        runSqlOperation(con -> {
            con = getConnection();
            PreparedStatement stmt = con.prepareStatement(d.getRemoveStmt());
            d.fillRemoveStmt(stmt);
            stmt.execute();
        });
    }

    @Override
    public void removeUser(User u){
        runSqlOperation(con -> {
            String getDevicesQuery = "SELECT * FROM User_device WHERE belongs_to=?";
            String deleteDevicesCommand = "DELETE FROM User_device WHERE belongs_to=?";
            List<Integer> certificateList = new ArrayList<>();
            con = getConnection();
            PreparedStatement sqlQuery = con.prepareStatement(getDevicesQuery);
            PreparedStatement sqlStmt = con.prepareStatement(u.getRemoveStmt());
            PreparedStatement sqlDeleteDevices = con.prepareStatement(deleteDevicesCommand);

            con.setAutoCommit(false);
            // revoke all devices
            sqlQuery.setInt(1, u.id);
            ResultSet res = sqlQuery.executeQuery();
            while (res.next()) {
                certificateList.add(res.getInt("ID"));
            }

            sqlDeleteDevices.setInt(1, u.id);
            sqlDeleteDevices.execute();
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();

            certificateList.forEach(authAdmin::revokeCertificate);
        });
    }

    @Override
    public Ticket addDevice(UserDevice d) {
        return runSqlOperation(con -> {
            String addTicket = "INSERT INTO Ticket (ticket, belongs_device, created_on) VALUES (?,LAST_INSERT_ID(), NOW())";
            Ticket result = new Ticket();
            result.ticket = Ticket.generateTicket();
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
            return result;
        });
    }

    @Override
    public void removeDevice(UserDevice u){
        runSqlOperation(con -> {
            con = getConnection();
            PreparedStatement sqlStmt = con.prepareStatement(u.getRemoveStmt());
            con.setAutoCommit(false);
            u.fillRemoveStmt(sqlStmt);
            sqlStmt.execute();
            con.commit();
            authAdmin.revokeCertificate(u.id);
        });
    }

    @Override
    public Data[] get(DataFactory df) {
        return runSqlOperation(con -> {
            PreparedStatement stmt = con.prepareStatement(df.getQuery());
            ResultSet rs = stmt.executeQuery();
            List<Data> result = df.createDataList(rs);
            return result.toArray(new Data[result.size()]);
        });
    }

    @Override
    public void moveItem(FoodItem item, int loc) {
        runSqlOperation(con -> {
                String sqlString = "UPDATE Food_item SET stored_in=? WHERE ID=?";
                PreparedStatement stmt = con.prepareStatement(sqlString);
                stmt.setInt(1, loc);
                stmt.setInt(2, item.id);
                stmt.executeQuery();
        });
    }

    <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        Connection con = null;
        try {
            con = getConnection();
            return client.apply(con);
        } catch (SQLException e) {
            LOG.error("Error during sql operation", e);
            rollback(con);
            return null;
        } finally {
            close(con);
        }
    }

    void runSqlOperation(ConsumerWithExceptions<Connection, SQLException> client) {
        runSqlOperation(con -> {
            client.accept(con);
            return null;
        });
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
