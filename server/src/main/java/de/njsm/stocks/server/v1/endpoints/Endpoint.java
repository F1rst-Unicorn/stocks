package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

class Endpoint {

    final DatabaseHandler handler;

    final UserContextFactory contextFactory;

    public Endpoint(DatabaseHandler handler,
                    UserContextFactory contextFactory) {
        this.handler = handler;
        this.contextFactory = contextFactory;
    }
}
