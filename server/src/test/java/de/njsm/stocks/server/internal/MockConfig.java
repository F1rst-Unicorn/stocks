package de.njsm.stocks.server.internal;

import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.auth.MockAuthAdmin;

public class MockConfig extends Config {

    protected AuthAdmin ca;

    public MockConfig() {
        ca = new MockAuthAdmin();
    }

    @Override
    public AuthAdmin getCertAdmin() {
        return ca;
    }
}
