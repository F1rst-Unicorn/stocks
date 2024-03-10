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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.server.util.FallibleOperationWrapper;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler implements FallibleOperationWrapper<DSLContext, SQLException> {

    private final ConnectionFactory connectionFactory;

    protected Principals principals;

    public FailSafeDatabaseHandler(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setPrincipals(Principals principals) {
        this.principals = principals;
    }

    public StatusCode commit() {
        return new ConnectionHandler(connectionFactory).commit();
    }

    public StatusCode rollback() {
        return new ConnectionHandler(connectionFactory).rollback();
    }

    public StatusCode setReadOnly() {
        try {
            connectionFactory.initConnection();
        } catch (SQLException e) {
            return StatusCode.DATABASE_UNREACHABLE;
        }
        return new ConnectionHandler(connectionFactory).setReadOnly();
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.DATABASE_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException>
    wrap(FunctionWithExceptions<DSLContext, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            try {
                Connection con = connectionFactory.getConnection();
                con.setAutoCommit(false);
                if (con.getTransactionIsolation() != Connection.TRANSACTION_SERIALIZABLE)
                    con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                var settings = new Settings().withReturnAllOnUpdatableRecord(true);
                DSLContext context = DSL.using(con, SQLDialect.POSTGRES, settings);
                return client.apply(context);
            } catch (RuntimeException e) {
                return ConnectionHandler.lookForSqlException(e);
            } catch (SQLException e) {
                if (ConnectionHandler.isSerialisationConflict(e))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else
                    throw e;
            }
        };
    }
}
