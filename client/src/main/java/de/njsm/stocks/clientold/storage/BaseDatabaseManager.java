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

package de.njsm.stocks.clientold.storage;

import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BaseDatabaseManager {

    private static final Logger LOG = LogManager.getLogger(BaseDatabaseManager.class);

    public void runSqlScript(List<String> script) throws DatabaseException {
        LOG.info("Running script");
        Connection c = null;

        try {
            c = getConnectionWithoutAutoCommit();
            Statement stmt = c.createStatement();
            for (String command : script) {
                stmt.execute(command);
            }
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new DatabaseException("Could complete SQL script", e);
        } finally {
            close(c);
        }
    }

    void runCommand(Consumer<DSLContext> command) throws DatabaseException {
        try (Connection connection = getConnection()) {
            DSLContext c = DSL.using(connection);
            command.accept(c);
        } catch (DataAccessException | SQLException e) {
            throw new DatabaseException(e);
        }
    }

    <T> T runFunction(Function<DSLContext, T> function) throws DatabaseException {
        try (Connection connection = getConnection()) {
            DSLContext c = DSL.using(connection);
            return function.apply(c);
        } catch (DataAccessException | SQLException e) {
            throw new DatabaseException(e);
        }
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + Configuration.DB_PATH);
    }

    DSLContext getContext() throws SQLException {
        return DSL.using(getConnection());
    }

    Connection getConnectionWithoutAutoCommit() throws SQLException {
        Connection result = getConnection();
        result.setAutoCommit(false);
        return result;
    }

    static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                LOG.error("Error rolling back connection", e);
            }
        }
    }
}
