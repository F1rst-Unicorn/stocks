package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseSqlDatabaseHandler {

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

    protected abstract <R> Validation<StatusCode, R> runQuery(FunctionWithExceptions<DSLContext, Validation<StatusCode, R>, SQLException> client);

    StatusCode runCommand(FunctionWithExceptions<DSLContext, StatusCode, SQLException> client) {
        Validation<StatusCode, StatusCode> result = runQuery(con -> {
            StatusCode code = client.apply(con);
            if (code == StatusCode.SUCCESS) {
                return Validation.success(code);
            } else {
                return Validation.fail(code);
            }
        });
        if (result.isFail()) {
            return result.fail();
        } else {
            return result.success();
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

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    public void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOG.error("Error while rollback", e1);
            }
        }
    }

}
