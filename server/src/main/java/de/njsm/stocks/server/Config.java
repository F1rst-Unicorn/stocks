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
    static final String DB_VALIDITY_KEY = "de.njsm.stocks.internal.ticketValidityTimeInMinutes";

    static final String POSTGRESQL_CONFIG_PREFIX = "de.njsm.stocks.server.v2.db.postgres.";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private Properties dbProperties;
    private int ticketValidity;

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);

        dbProperties = filterPostgresqlProperties(p);

        String rawTicketValidity = p.getProperty(DB_VALIDITY_KEY);
        try {
            ticketValidity = Integer.parseInt(rawTicketValidity);
        } catch (NumberFormatException e) {
            LOG.error("ticket validity is not an integer", e);
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
