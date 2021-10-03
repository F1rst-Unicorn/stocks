/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public class Endpoint {

    private static final Logger LOG = LogManager.getLogger(Endpoint.class);

    protected static Principals getPrincipals(HttpServletRequest request) {
        return (Principals) request.getAttribute(PrincipalFilter.STOCKS_PRINCIPAL);
    }

    public static boolean isValid(String parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter != null && ! parameter.isEmpty()) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    public boolean isValidOrEmpty(String parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter != null) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    public static boolean isValid(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter > 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    public boolean isValidName(String value, String name) {
        LOG.debug("Checking parameter " + name);

        return isValid(value, name) && Principals.isNameValid(value);
    }

    public static boolean isValidVersion(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter >= 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    public boolean isValidInstant(String rawInstant, String name) {
        LOG.debug("Checking parameter " + name);

        try {
            InstantDeserialiser.parseString(rawInstant);
            return true;
        } catch (IOException e) {
            LOG.info("Request is invalid as " + name + " has value '" + rawInstant + "'");
            return false;
        }
    }

    public static Optional<Instant> parseToInstant(String rawInstant, String name) {
        if (rawInstant == null || rawInstant.isEmpty()) {
            LOG.debug("Defaulting missing instant to epoch");
            return Optional.of(Instant.EPOCH);
        }

        try {
            return Optional.of(InstantDeserialiser.parseString(rawInstant));
        } catch (IOException e) {
            LOG.info("Request is invalid as " + name + " has value '" + rawInstant + "'");
            return Optional.empty();
        }
    }
}
