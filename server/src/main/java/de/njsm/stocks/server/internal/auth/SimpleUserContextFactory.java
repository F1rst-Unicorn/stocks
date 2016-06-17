package de.njsm.stocks.server.internal.auth;

import javax.servlet.http.HttpServletRequest;

public class SimpleUserContextFactory implements ContextFactory {

    public Principals getPrincipals(HttpServletRequest request) {
        return new Principals("test_user", "test_device", 0, 0);
    }

}
