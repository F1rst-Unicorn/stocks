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

    public FailSafeDatabaseHandler(ConnectionFactory connectionFactory,
                                   String resourceIdentifier) {
        super(connectionFactory);
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
            Validation<StatusCode, O> result = DSL.using(con, SQLDialect.POSTGRES_10).transactionResult(configuration -> {
                DSLContext context = DSL.using(configuration);
                return client.apply(context);
            });
            close(con);
            return result;
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
