package de.njsm.stocks.client.storage;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BaseDatabaseManager {

    private static final Logger LOG = LogManager.getLogger(BaseDatabaseManager.class);

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

    void runCommand(Consumer<DSLContext> command) throws DatabaseException {
        try (DSLContext c = getContext()) {
            command.accept(c);
        } catch (DataAccessException | SQLException e) {
            throw new DatabaseException(e);
        }
    }

    <T> T runFunction(Function<DSLContext, T> function) throws DatabaseException {
        try (DSLContext c = getContext()) {
            return function.apply(c);
        } catch (DataAccessException | SQLException e) {
            throw new DatabaseException(e);
        }
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    DSLContext getContext() throws SQLException {
        return DSL.using(getConnection());
    }

    Connection getConnectionWithoutAutoCommit() throws SQLException {
        Connection result = getConnection();
        result.setAutoCommit(false);
        return result;
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
