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

package de.njsm.stocks.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;

public class Config {

    private static final Logger LOG = LogManager.getLogger(Config.class);

    static final String DB_ADDRESS_KEY = "de.njsm.stocks.server.v2.db.host";
    static final String DB_PORT_KEY = "de.njsm.stocks.server.v2.db.port";
    static final String DB_NAME_KEY = "de.njsm.stocks.server.v2.db.name";
    static final String DB_CIRCUIT_BREAKER_TIMEOUT_KEY = "de.njsm.stocks.server.v2.circuitbreaker.timeout";
    static final String DB_VALIDITY_KEY = "de.njsm.stocks.internal.ticketValidityTimeInMinutes";

    static final String POSTGRESQL_CONFIG_PREFIX = "de.njsm.stocks.server.v2.db.postgres.";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private int circuitBreakerTimeout;
    private Properties dbProperties;
    private int ticketValidity;

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);

        String rawCircuitBreakerTimeout = p.getProperty(DB_CIRCUIT_BREAKER_TIMEOUT_KEY);
        try {
            circuitBreakerTimeout = Integer.parseInt(rawCircuitBreakerTimeout);
        } catch (NumberFormatException e) {
            LOG.error(DB_CIRCUIT_BREAKER_TIMEOUT_KEY + " is not an integer", e);
            throw e;
        }

        dbProperties = filterPostgresqlProperties(p);

        String rawTicketValidity = p.getProperty(DB_VALIDITY_KEY);
        try {
            ticketValidity = Integer.parseInt(rawTicketValidity);
        } catch (NumberFormatException e) {
            LOG.error(DB_VALIDITY_KEY + " is not an integer", e);
            throw e;
        }
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public int getCircuitBreakerTimeout() {
        return circuitBreakerTimeout;
    }

    public Properties getDbProperties() {
        return dbProperties;
    }

    public int getTicketValidity() {
        return ticketValidity;
    }

    private Properties filterPostgresqlProperties(Properties config) {
        Properties result = new Properties();

        for (Map.Entry<Object, Object> entry: config.entrySet()) {
            if (entry.getKey() instanceof String &&
                    entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(POSTGRESQL_CONFIG_PREFIX) &&
                        ! value.isEmpty()) {
                    result.put(
                            key.replace(POSTGRESQL_CONFIG_PREFIX, ""),
                            entry.getValue()
                    );
                }
            }
        }
        return result;
    }
}
