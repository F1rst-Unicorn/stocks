package de.njsm.stocks.server.internal.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Producer;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler extends BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(FailSafeDatabaseHandler.class);

    private static final String RESOURCE_IDENTIFIER = "database";

    private String resourceIdentifier;

    FailSafeDatabaseHandler(String url,
                                   String username,
                                   String password) {
        super(url, username, password);
        resourceIdentifier = RESOURCE_IDENTIFIER;
    }

    FailSafeDatabaseHandler(String url,
                                   String username,
                                   String password,
                                   String resourceIdentifier) {
        super(url, username, password);
        this.resourceIdentifier = resourceIdentifier;
    }

    public boolean isCircuitBreakerOpen() {
        Producer<Void> dummy = () -> null;
        return new HystrixFunction<>(resourceIdentifier, dummy)
                .isCircuitBreakerOpen();
    }

    @Override
    protected <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        HystrixFunction<R> producer = new HystrixFunction<>(resourceIdentifier,
                () -> super.runSqlOperation(client));

        try {
            return producer.execute();

        } catch (HystrixRuntimeException e) {
            LOG.error("circuit breaker error", e);
            return null;
        }
    }
}
