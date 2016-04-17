package de.njsm.stocks.internal;

import de.njsm.stocks.internal.auth.CertificateAdmin;
import de.njsm.stocks.internal.auth.ContextFactory;
import de.njsm.stocks.internal.auth.SimpleCertificateAdmin;
import de.njsm.stocks.internal.auth.SimpleUserContextFactory;
import de.njsm.stocks.internal.db.DatabaseHandler;
import de.njsm.stocks.internal.db.SimpleDatabaseHandler;
import de.njsm.stocks.internal.db.SqlDatabaseHandler;

import java.util.Properties;

public class Config {

    public ContextFactory getContextFactory() {
        return new SimpleUserContextFactory();
    }

    public DatabaseHandler getDbHandler() {
        return new SqlDatabaseHandler();
    }

    public CertificateAdmin getCertAdmin() {
        return new SimpleCertificateAdmin();
    }
}
