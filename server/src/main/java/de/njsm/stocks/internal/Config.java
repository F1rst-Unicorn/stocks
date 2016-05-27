package de.njsm.stocks.internal;

import de.njsm.stocks.internal.auth.*;
import de.njsm.stocks.internal.db.DatabaseHandler;
import de.njsm.stocks.internal.db.SqlDatabaseHandler;

import java.util.logging.Logger;

public class Config {

    public ContextFactory getContextFactory() {
        return new HttpsUserContextFactory();
    }

    public DatabaseHandler getDbHandler() {
        return new SqlDatabaseHandler();
    }

    public CertificateAdmin getCertAdmin() {
        return new X509CertificateAdmin();
    }

    public Logger getLog() {
        return Logger.getLogger("stocks");
    }
}
