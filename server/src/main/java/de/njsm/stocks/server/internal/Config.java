package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.X509CertificateAdmin;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;

import java.util.logging.*;

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
        Logger result = Logger.getLogger("stocks-client");
        for (Handler h : result.getHandlers()) {
            result.removeHandler(h);
        }
        result.setLevel(Level.ALL);
        result.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();

        Formatter formatter = new Formatter();
        handler.setFormatter(formatter);
        handler.setLevel(Level.ALL);
        result.addHandler(handler);
        return result;
    }
}
