package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler extends BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(FailSafeDatabaseHandler.class);

    private static final String RESOURCE_IDENTIFIER = "database";

    FailSafeDatabaseHandler(String url,
                                   String username,
                                   String password) {
        super(url, username, password);
    }

    @Override
    protected <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        HystrixFunction<R> producer = new HystrixFunction<>(RESOURCE_IDENTIFIER,
                () -> super.runSqlOperation(client));

        try {
            return producer.execute();

        } catch (HystrixRuntimeException e) {
            LOG.error("circuit breaker error", e);
            if (producer.isCircuitBreakerOpen()) {
                LOG.error("opening circuit breaker");
            }
            return null;
        }
    }
}
