package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.util.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

class Endpoint {

    final DatabaseHandler handler;

    final UserContextFactory contextFactory;

    public Endpoint(DatabaseHandler handler,
                    UserContextFactory contextFactory) {
        this.handler = handler;
        this.contextFactory = contextFactory;
    }

    protected void logAccess(Logger targetLog,
                             HttpServletRequest request,
                             String description) {
        Principals userInfo = contextFactory.getPrincipals(request);
        String logEntry = String.format("%s %s",
                userInfo.getReadableString(),
                description);

        targetLog.info(logEntry);
    }
}
