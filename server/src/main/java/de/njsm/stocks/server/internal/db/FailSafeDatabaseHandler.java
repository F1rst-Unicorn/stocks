package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler extends BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(FailSafeDatabaseHandler.class);

    private String resourceIdentifier;

    FailSafeDatabaseHandler(String url,
                                   String username,
                                   String password,
                                   String resourceIdentifier) {
        super(url, username, password);
        this.resourceIdentifier = resourceIdentifier;
    }

    public boolean isCircuitBreakerOpen() {
        return new HystrixFunction<>(resourceIdentifier, null)
                .isCircuitBreakerOpen();
    }

    @Override
    protected <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        HystrixFunction<R> producer = new HystrixFunction<>(resourceIdentifier,
                () -> runSafeSqlOperation(client));

        try {
            return producer.execute();

        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                LOG.error("circuit breaker still open");
            } else {
                LOG.error("circuit breaker error", e);
            }
            return null;
        }
    }

    private <R> R runSafeSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) throws SQLException {
        Connection con = null;
        try {
            con = getConnection();
            return client.apply(con);
        } catch (SQLException e) {
            rollback(con);
            throw e;
        } finally {
            close(con);
        }
    }



}
