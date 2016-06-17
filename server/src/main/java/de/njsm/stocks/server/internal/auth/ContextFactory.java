package de.njsm.stocks.server.internal.auth;

import javax.servlet.http.HttpServletRequest;

public interface ContextFactory {

    Principals getPrincipals(HttpServletRequest request);
}
