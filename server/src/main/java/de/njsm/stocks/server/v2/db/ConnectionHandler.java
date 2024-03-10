/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.FallibleOperationWrapper;
import fj.data.Validation;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionHandler implements FallibleOperationWrapper<Connection, SQLException> {

    private static final String SERIALISATION_FAILURE_SQL_STATE = "40001";

    private static final String CHECK_VIOLATION_SQL_STATE = "23514";

    private final ConnectionFactory connectionFactory;

    ConnectionHandler(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    StatusCode commit() {
        return runCommand(con -> {
            connectionFactory.reset();
            con.setAutoCommit(false);
            con.commit();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    StatusCode rollback() {
        return runCommand(con -> {
            connectionFactory.reset();
            con.setAutoCommit(false);
            con.rollback();
            con.close();
            return StatusCode.SUCCESS;
        });
    }

    StatusCode setReadOnly() {
        return runCommand(con -> {
            con.setReadOnly(true);
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.DATABASE_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException> wrap(FunctionWithExceptions<Connection, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            try {
                var connection = connectionFactory.getExistingConnection();
                if (connection.isPresent()) {
                    return client.apply(connection.get());
                } else {
                    LOG.debug("no existing connection");
                    return Validation.fail(StatusCode.DATABASE_UNREACHABLE);
                }
            } catch (RuntimeException e) {
                return lookForSqlException(e);
            } catch (SQLException e) {
                if (isSerialisationConflict(e))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else if (isCheckViolation(e))
                    return Validation.fail(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION);
                else
                    throw e;
            }
        };
    }

    private static boolean isCheckViolation(SQLException e) {
        String sqlState = e.getSQLState();

        if (sqlState != null && sqlState.equals(CHECK_VIOLATION_SQL_STATE)) {
            LOG.info("Constraint violation, transaction was rolled back: " + e.getMessage());
            return true;
        }

        return false;
    }

    static <O> Validation<StatusCode, O> lookForSqlException(RuntimeException e) throws RuntimeException {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException)
                if (isSerialisationConflict((SQLException) cause))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else if (isCheckViolation((SQLException) cause))
                    return Validation.fail(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION);

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
