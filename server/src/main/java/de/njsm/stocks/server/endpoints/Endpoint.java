package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.Principals;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

class Endpoint {

    final Config c;
    final DatabaseHandler handler;

    Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }

    public Endpoint(Config c) {
        this.c = c;
        handler = c.getDbHandler();
    }

    protected void logAccess(Logger targetLog,
                             HttpServletRequest request,
                             String description) {
        Principals userInfo = c.getContextFactory().getPrincipals(request);
        String logEntry = String.format("%s %s",
                userInfo.getReadableString(),
                description);

        targetLog.info(logEntry);
    }
}
