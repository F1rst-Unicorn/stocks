package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.auth.X509CertificateAdmin;

import java.util.Properties;

public class Config {

    private static final String DB_ADDRESS_KEY = "de.njsm.stocks.internal.db.databaseAddress";
    private static final String DB_PORT_KEY = "de.njsm.stocks.internal.db.databasePort";
    private static final String DB_NAME_KEY = "de.njsm.stocks.internal.db.databaseName";
    private static final String DB_USERNAME_KEY = "de.njsm.stocks.internal.db.databaseUsername";
    private static final String DB_PASSWORD_KEY = "de.njsm.stocks.internal.db.databasePassword";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);
        dbUsername = p.getProperty(DB_USERNAME_KEY);
        dbPassword = p.getProperty(DB_PASSWORD_KEY);
    }

    public AuthAdmin getCertAdmin() {
        return new X509CertificateAdmin();
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
}
