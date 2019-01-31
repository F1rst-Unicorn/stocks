package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.HystrixWrapper;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionCloser implements HystrixWrapper<Connection, SQLException> {

    private String resourceIdentifier;

    private Connection connection;

    public ConnectionCloser(String resourceIdentifier, Connection connection) {
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
}
