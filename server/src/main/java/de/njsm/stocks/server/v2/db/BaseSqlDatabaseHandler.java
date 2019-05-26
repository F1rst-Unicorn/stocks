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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseSqlDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(BaseSqlDatabaseHandler.class);

    private final Connection connection;

    public BaseSqlDatabaseHandler(Connection connection) {
        this.connection = connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("DB driver not present", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return connection;
    }

    protected void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOG.error("Error while rollback", e1);
            }
        }
    }

}
