package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;

import javax.servlet.http.HttpServletRequest;

public interface UserContextFactory {

    Principals getPrincipals(HttpServletRequest request);

}
