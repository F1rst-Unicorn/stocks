package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    protected boolean isValidInstant(String rawInstant, String name) {
        LOG.debug("Checking parameter " + name);

        try {
            InstantDeserialiser.parseString(rawInstant);
            return true;
        } catch (IOException e) {
            LOG.info("Request is invalid as " + name + " has value '" + rawInstant + "'");
            return false;
        }
    }
}
