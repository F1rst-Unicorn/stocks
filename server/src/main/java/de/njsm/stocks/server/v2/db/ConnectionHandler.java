/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.HystrixWrapper;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHandler implements HystrixWrapper<Connection, SQLException>, TransactionHandler {

    private static final String SERIALISATION_FAILURE_SQL_STATE = "40001";

    private final String resourceIdentifier;

    private final ConnectionFactory connectionFactory;

    private final int timeout;

    public ConnectionHandler(String resourceIdentifier,
                             ConnectionFactory connectionFactory,
                             int timeout) {
        this.resourceIdentifier = resourceIdentifier;
        this.connectionFactory = connectionFactory;
        this.timeout = timeout;
    }

    @Override
    public StatusCode commit() {
        return runCommand(con -> {
            connectionFactory.reset();
            con.setAutoCommit(false);
            con.commit();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public StatusCode rollback() {
        return runCommand(con -> {
            connectionFactory.reset();
            con.setAutoCommit(false);
            con.rollback();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    @Override
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
    public int getCircuitBreakerTimeout() {
        return timeout;
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.DATABASE_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException> wrap(FunctionWithExceptions<Connection, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            try {
                Connection con = connectionFactory.getConnection();
                return client.apply(con);
            } catch (RuntimeException e) {
                return lookForSqlException(e);
            } catch (SQLException e) {
                if (isSerialisationConflict(e))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else
                    throw e;
            }
        };
    }

    static <O> Validation<StatusCode, O> lookForSqlException(RuntimeException e) throws RuntimeException {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException)
                if (isSerialisationConflict((SQLException) cause))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);

            cause = cause.getCause();
        }
        throw e;
    }

    static boolean isSerialisationConflict(SQLException cause) {
        String sqlState = cause.getSQLState();

        if (sqlState != null && sqlState.equals(SERIALISATION_FAILURE_SQL_STATE)) {
            LOG.info("Serialisation error, transaction was rolled back");
            return true;
        }

        return false;
    }
}
