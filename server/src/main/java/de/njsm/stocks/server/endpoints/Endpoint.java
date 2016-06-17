package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;

public class Endpoint {

    protected final Config c;
    protected final SqlDatabaseHandler handler;


    public Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }
}
