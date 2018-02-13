package de.njsm.stocks.server.internal.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(BaseSqlDatabaseHandler.class);

    private final String url;

    private final String username;

    private final String password;


    public BaseSqlDatabaseHandler(String url,
                                  String username,
                                  String password) {
        this.username = username;
        this.password = password;
        this.url = url;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    protected <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
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
