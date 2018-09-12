package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class Endpoint {

    private static final Logger LOG = LogManager.getLogger(RegistrationEndpoint.class);

    protected Principals getPrincipals(HttpServletRequest request) {
        return (Principals) request.getAttribute(PrincipalFilter.STOCKS_PRINCIPAL);
    }

    protected boolean isValid(String parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter != null && ! parameter.isEmpty()) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    protected boolean isValid(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter > 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    protected boolean isValidVersion(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter >= 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }
}
