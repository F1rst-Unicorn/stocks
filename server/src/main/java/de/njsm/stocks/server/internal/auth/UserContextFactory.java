package de.njsm.stocks.server.internal.auth;

import javax.servlet.http.HttpServletRequest;

public interface UserContextFactory {

    Principals getPrincipals(HttpServletRequest request);

}
