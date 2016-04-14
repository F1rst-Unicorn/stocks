package de.njsm.stocks.internal;

import de.njsm.stocks.internal.auth.ContextFactory;
import de.njsm.stocks.internal.auth.SimpleUserContextFactory;

public class Config {

    public ContextFactory getContextFactory() {
        return new SimpleUserContextFactory();
    }
}
