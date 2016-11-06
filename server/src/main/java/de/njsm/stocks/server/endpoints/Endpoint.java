package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;

class Endpoint {

    final Config c;
    final SqlDatabaseHandler handler;

    // This global initialisation is done here, because this is one
    // of the first classes loaded by the container
    static {
        if (null == System.getProperty("log4j.configurationFile")) {
            System.getProperties().setProperty("log4j.configurationFile", "/etc/stocks-server/log4j.xml");
        }
    }

    Endpoint() {
        c = new Config();
        handler = c.getDbHandler();
    }
}
