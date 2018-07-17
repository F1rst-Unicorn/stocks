package de.njsm.stocks.server.v1.internal.business;

import javax.servlet.http.HttpServletRequest;

public interface UserContextFactory {

    de.njsm.stocks.server.util.Principals getPrincipals(HttpServletRequest request);

}
