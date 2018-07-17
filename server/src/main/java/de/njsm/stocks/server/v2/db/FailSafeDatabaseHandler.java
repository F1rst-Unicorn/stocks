package de.njsm.stocks.server.v2.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.server.util.HystrixFunction;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler extends BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(FailSafeDatabaseHandler.class);

    private String resourceIdentifier;

    public FailSafeDatabaseHandler(String url,
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
    public <R> Validation<StatusCode, R> runQuery(FunctionWithExceptions<DSLContext, Validation<StatusCode, R>, SQLException> client) {
        HystrixFunction<Validation<StatusCode, R>, SQLException> producer = new HystrixFunction<>(resourceIdentifier,
                () -> runAndClose(client));

        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof RuntimeException) {
                LOG.error("circuit breaker still open");
            } else {
                LOG.error("circuit breaker error", e);
            }
            return Validation.fail(StatusCode.DATABASE_UNREACHABLE);
        }
    }


    @Override
    public <R> R runSqlOperation(FunctionWithExceptions<Connection, R, SQLException> client) {
        HystrixFunction<R, SQLException> producer = new HystrixFunction<>(resourceIdentifier,
                () -> runAndCloseSqlCommand(client));

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

    private <R> Validation<StatusCode, R> runAndClose(FunctionWithExceptions<DSLContext, Validation<StatusCode, R>, SQLException> client) throws SQLException {
        Connection con = null;
        try {
            con = getConnection();
            return DSL.using(con, SQLDialect.MARIADB).transactionResult(configuration -> {
                DSLContext context = DSL.using(configuration);
                return client.apply(context);
            });
        } catch (SQLException |
                 DataAccessException e) {
            rollback(con);
            throw e;
        } finally {
            close(con);
        }
    }

    private <R> R runAndCloseSqlCommand(FunctionWithExceptions<Connection, R, SQLException> client) throws SQLException {
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
