package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;

import javax.servlet.http.HttpServletRequest;

public class Endpoint {

    protected Principals getPrincipals(HttpServletRequest request) {
        return (Principals) request.getAttribute(PrincipalFilter.STOCKS_PRINCIPAL);
    }

}
