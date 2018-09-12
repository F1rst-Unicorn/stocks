package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(BaseSqlDatabaseHandler.class);

    private final ConnectionFactory connectionFactory;

    public BaseSqlDatabaseHandler(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
        }
    }

    @Deprecated
    public void runSqlOperation(ConsumerWithExceptions<Connection, SQLException> client) {
        runSqlOperation(con -> {
            client.accept(con);
            return null;
        });
    }

    @Deprecated
    public abstract <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client);

    void close(Connection con) {
        if (con != null) {
            try {
                connectionFactory.returnConnection(con);
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOG.error("Error while rollback", e1);
            }
        }
    }

}
