package de.njsm.stocks.server.util;

import javax.servlet.http.HttpServletRequest;

public interface UserContextFactory {

    de.njsm.stocks.server.util.Principals getPrincipals(HttpServletRequest request);

}
