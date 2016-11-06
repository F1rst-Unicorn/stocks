package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.auth.MockAuthAdmin;

import java.util.Properties;

public class MockConfig extends Config {

    protected AuthAdmin ca;

    public MockConfig(Properties p) {
        super(p);
        ca = new MockAuthAdmin();
    }

    public MockConfig() {
        ca = new MockAuthAdmin();
    }

    @Override
    public AuthAdmin getCertAdmin() {
        return ca;
    }
}
