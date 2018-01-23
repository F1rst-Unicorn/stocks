package de.njsm.stocks.server.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class Config {

    private static final Logger LOG = LogManager.getLogger(Config.class);

    static final String DB_ADDRESS_KEY = "de.njsm.stocks.internal.db.databaseAddress";
    static final String DB_PORT_KEY = "de.njsm.stocks.internal.db.databasePort";
    static final String DB_NAME_KEY = "de.njsm.stocks.internal.db.databaseName";
    static final String DB_USERNAME_KEY = "de.njsm.stocks.internal.db.databaseUsername";
    static final String DB_PASSWORD_KEY = "de.njsm.stocks.internal.db.databasePassword";
    static final String DB_VALIDITY_KEY = "de.njsm.stocks.internal.ticketValidityTimeInMinutes";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    private int ticketValidity;

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);
        dbUsername = p.getProperty(DB_USERNAME_KEY);
        dbPassword = p.getProperty(DB_PASSWORD_KEY);
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

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public int getTicketValidity() {
        return ticketValidity;
    }
}
