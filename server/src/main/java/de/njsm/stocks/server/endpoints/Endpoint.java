package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.auth.Principals;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

class Endpoint {

    final Config c;
    final SqlDatabaseHandler handler;

    Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }

    public void logAccess(Logger targetLog,
                          HttpServletRequest request,
                          String description) {
        Principals userInfo = c.getContextFactory().getPrincipals(request);
        String logEntry = String.format("%s@%s %s",
                userInfo.getUsername(),
                userInfo.getDeviceName(),
                description);

        targetLog.info(logEntry);
    }
}
