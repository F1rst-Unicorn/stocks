package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.ConsumerWithExceptions;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

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

    protected <R> Validation<StatusCode, R> runOperation(FunctionWithExceptions<DSLContext, Validation<StatusCode, R>, SQLException> client) {
        Connection con = null;
        try {
            con = getConnection();
            DSLContext context = DSL.using(con, SQLDialect.MARIADB);
            return client.apply(context);
        } catch (SQLException e) {
            LOG.error("Error during sql operation", e);
            rollback(con);
            return Validation.fail(StatusCode.GENERAL_ERROR);
        } finally {
            close(con);
        }
    }

    protected StatusCode runOperation(ConsumerWithExceptions<DSLContext, SQLException> client) {
        Validation<StatusCode, StatusCode> result = runOperation(con -> {
            client.accept(con);
            return Validation.success(StatusCode.SUCCESS);
        });
        if (result.isFail()) {
            return result.fail();
        } else {
            return result.success();
        }
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

    public void runSqlOperation(ConsumerWithExceptions<Connection, SQLException> client) {
        runSqlOperation(con -> {
            client.accept(con);
            return null;
        });
    }

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
