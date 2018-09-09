package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;

class Endpoint extends de.njsm.stocks.server.v2.web.Endpoint {

    final DatabaseHandler handler;

    public Endpoint(DatabaseHandler handler) {
        this.handler = handler;
    }
}
