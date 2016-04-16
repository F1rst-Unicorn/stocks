package de.njsm.stocks.internal;

import de.njsm.stocks.internal.auth.ContextFactory;
import de.njsm.stocks.internal.auth.SimpleUserContextFactory;
import de.njsm.stocks.internal.db.DatabaseHandler;
import de.njsm.stocks.internal.db.SimpleDatabaseHandler;
import de.njsm.stocks.internal.db.SqlDatabaseHandler;

public class Config {

    public ContextFactory getContextFactory() {
        return new SimpleUserContextFactory();
    }

    public DatabaseHandler getDbHandler() {
        return new SqlDatabaseHandler();
    }
}
