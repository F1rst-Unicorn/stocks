package de.njsm.stocks.endpoints;

import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.db.DatabaseHandler;

public class Endpoint {

    protected final Config c;
    protected final DatabaseHandler handler;


    public Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }
}
