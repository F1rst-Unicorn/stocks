package de.njsm.stocks.server.v2.db;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.HystrixWrapper;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHandler implements HystrixWrapper<Connection, SQLException> {

    private static final String SERIALISATION_FAILURE_SQL_STATE = "40001";

    private String resourceIdentifier;

    private Connection connection;

    public ConnectionHandler(String resourceIdentifier, Connection connection) {
        this.resourceIdentifier = resourceIdentifier;
        this.connection = connection;
    }

    public StatusCode commit() {
        return runCommand(con -> {
            con.setAutoCommit(false);
            con.commit();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode rollback() {
        return runCommand(con -> {
            con.setAutoCommit(false);
            con.rollback();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode setReadOnly() {
        return runCommand(con -> {
            con.setReadOnly(true);
            return StatusCode.SUCCESS;
        });
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
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException> wrap(FunctionWithExceptions<Connection, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            Connection con = connection;
            return client.apply(con);
        };
    }

    @Override
    public <O> Validation<StatusCode, O> handleException(HystrixRuntimeException e) {
        if (e.getCause() instanceof SQLException) {
            SQLException cause = (SQLException) e.getCause();

            if (cause.getSQLState().equals(SERIALISATION_FAILURE_SQL_STATE)) {
                LOG.warn("Serialisation error, transaction was rolled back");
                return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
            } else {
                return HystrixWrapper.super.handleException(e);
            }
        } else {
            return HystrixWrapper.super.handleException(e);
        }
    }


}