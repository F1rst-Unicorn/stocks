package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;

class Endpoint {

    final Config c;
    final SqlDatabaseHandler handler;

    Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }
}
