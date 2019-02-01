package de.njsm.stocks.server.v2.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.HystrixProducer;
import de.njsm.stocks.server.util.HystrixWrapper;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler extends BaseSqlDatabaseHandler implements HystrixWrapper<DSLContext, SQLException> {

    private static final Logger LOG = LogManager.getLogger(FailSafeDatabaseHandler.class);

    private String resourceIdentifier;

    public FailSafeDatabaseHandler(Connection connection,
                                   String resourceIdentifier) {
        super(connection);
        this.resourceIdentifier = resourceIdentifier;
    }

    boolean isCircuitBreakerOpen() {
        return new HystrixProducer<>(resourceIdentifier, null, null)
                .isCircuitBreakerOpen();
    }

    @Deprecated
    public <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        HystrixProducer<Connection, R, SQLException> producer = new HystrixProducer<>(resourceIdentifier,
                this::runAndCloseSqlCommand,
                client);

        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                LOG.error("circuit breaker still open", e);
            } else {
                LOG.error("circuit breaker error", e);
            }
            return null;
        }
    }

    public StatusCode commit() {
        try {
            return new ConnectionHandler(resourceIdentifier, getConnection()).commit();
        } catch (SQLException e) {
            LOG.error("This should not happen", e);
            return getDefaultErrorCode();
        }
    }

    public StatusCode rollback() {
        try {
            return new ConnectionHandler(resourceIdentifier, getConnection()).rollback();
        } catch (SQLException e) {
            LOG.error("This should not happen", e);
            return getDefaultErrorCode();
        }
    }

    public StatusCode setReadOnly() {
        try {
            return new ConnectionHandler(resourceIdentifier, getConnection()).setReadOnly();
        } catch (SQLException e) {
            LOG.error("This should not happen", e);
            return getDefaultErrorCode();
        }
    }

    @Override
    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.DATABASE_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException>
    wrap(FunctionWithExceptions<DSLContext, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            Connection con = getConnection();
            con.setAutoCommit(false);
            if (con.getTransactionIsolation() != Connection.TRANSACTION_SERIALIZABLE)
                con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            DSLContext context = DSL.using(con, SQLDialect.POSTGRES);
            return client.apply(context);
        };
    }

    @Deprecated
    private <R> ProducerWithExceptions<R, SQLException>
    runAndCloseSqlCommand(FunctionWithExceptions<Connection, R, SQLException> client) {
        return () -> {
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
        };
    }
}
