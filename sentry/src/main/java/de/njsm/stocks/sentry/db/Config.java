package de.njsm.stocks.sentry.db;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.*;

public class Config {

    private static final String DB_ADDRESS_KEY = "de.njsm.stocks.internal.db.databaseAddress";
    private static final String DB_PORT_KEY = "de.njsm.stocks.internal.db.databasePort";
    private static final String DB_NAME_KEY = "de.njsm.stocks.internal.db.databaseName";
    private static final String DB_USERNAME_KEY = "de.njsm.stocks.internal.db.databaseUsername";
    private static final String DB_PASSWORD_KEY = "de.njsm.stocks.internal.db.databasePassword";
    private static final String DB_VALIDITY_KEY = "de.njsm.stocks.internal.ticketValidityTimeInMinutes";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    private String dbValidity;

    private final Logger log = Logger.getLogger("stocks");

    public Config() {
        try {
            FileInputStream fis = new FileInputStream("/etc/stocks-server/stocks.properties");
            Properties p = new Properties();
            p.load(fis);
            readProperties(p);
            IOUtils.closeQuietly(fis);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to configure server!", e);
        }
    }

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);
        dbUsername = p.getProperty(DB_USERNAME_KEY);
        dbPassword = p.getProperty(DB_PASSWORD_KEY);
        dbValidity = p.getProperty(DB_VALIDITY_KEY);
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

    public String getDbValidity() {
        return dbValidity;
    }
}
