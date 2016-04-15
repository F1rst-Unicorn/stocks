package de.njsm.stocks.endpoints;

import de.njsm.stocks.internal.Config;
import de.njsm.stocks.internal.db.DatabaseHandler;

public class Endpoint {

    protected Config c;
    protected DatabaseHandler handler;


    public Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }
}
