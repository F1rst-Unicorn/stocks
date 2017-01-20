package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.auth.MockAuthAdmin;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import de.njsm.stocks.server.internal.db.SqlDatabaseHandler;
import org.mockito.Mockito;

import java.util.Properties;

public class MockConfig extends Config {

    protected AuthAdmin ca;
    protected DatabaseHandler db;

    public MockConfig(Properties p) {
        super(p);
        ca = new MockAuthAdmin();
        db = Mockito.mock(DatabaseHandler.class);
    }

    public MockConfig() {
        ca = new MockAuthAdmin();
        db = Mockito.mock(DatabaseHandler.class);
    }

    @Override
    public AuthAdmin getCertAdmin() {
        return ca;
    }

    @Override
    public DatabaseHandler getDbHandler() {
        return db;
    }
}
