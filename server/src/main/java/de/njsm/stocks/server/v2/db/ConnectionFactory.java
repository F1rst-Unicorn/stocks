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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ConnectionFactory {

    private static final Logger LOG = LogManager.getLogger(ConnectionFactory.class);

    private final DataSource pool;

    private Connection connection;

    public ConnectionFactory(DataSource pool) {
        this.pool = pool;
    }

    public void initConnection() throws SQLException {
        if (connection == null) {
            LOG.debug("Checking out new connection");
            connection = pool.getConnection();
            LOG.debug("Checked out new connection");
        }
    }

    /**
     * Opens a new connection only on first call
     */
    public Connection getConnection() throws SQLException {
        initConnection();
        return connection;
    }

    public Optional<Connection> getExistingConnection() {
        return Optional.ofNullable(connection);
    }

    public void reset() {
        connection = null;
    }
}
