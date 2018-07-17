package de.njsm.stocks.server.util;

import javax.servlet.http.HttpServletRequest;

import static de.njsm.stocks.server.v2.web.PrincipalFilter.parseSubjectName;

public class HttpsUserContextFactory implements UserContextFactory {

    public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";

    @Override
    public Principals getPrincipals(HttpServletRequest request) {
        String clientName = request.getHeader(SSL_CLIENT_KEY);
        return parseSubjectName(clientName);
    }

}
