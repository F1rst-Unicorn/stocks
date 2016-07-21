package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.X509CertificateAdmin;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;

import java.util.logging.Logger;

public class Config {

    public HttpsUserContextFactory getContextFactory() {
        return new HttpsUserContextFactory();
    }

    public SqlDatabaseHandler getDbHandler() {
        return new SqlDatabaseHandler();
    }

    public X509CertificateAdmin getCertAdmin() {
        return new X509CertificateAdmin();
    }

    public Logger getLog() {
        return Logger.getLogger("stocks");
    }
}
