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
}
