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

package de.njsm.stocks.server.v2.web.servlet;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class PrincipalFilter implements ContainerRequestFilter {

    private static final Logger LOG = LogManager.getLogger(PrincipalFilter.class);

    public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";

    public static final String STOCKS_PRINCIPAL = "de.njsm.stocks.server.util.Principals";

    public static final String ORIGIN = "X-ORIGIN";

    public static final String ORIGIN_SENTRY = "sentry";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (requestContext.getHeaderString(ORIGIN).equals(ORIGIN_SENTRY)) {
            LOG.info("Anonymous user " +
                    requestContext.getMethod().toLowerCase() + "s " +
                    requestContext.getUriInfo().getPath());
        } else {
            addPrincipals(requestContext);

        }
    }

    private void addPrincipals(ContainerRequestContext requestContext) {
        String headerContent = requestContext.getHeaderString(SSL_CLIENT_KEY);
        Validation<StatusCode, Principals> principals = parseSubjectName(headerContent);

        if (principals.isFail()) {
            LOG.error("Got invalid request with SSL header '"
                    + SSL_CLIENT_KEY + ": " + headerContent);
            requestContext.abortWith(Response.status(403).build());
            return;
        }

        grantAccess(requestContext, principals.success());
    }

    private void grantAccess(ContainerRequestContext requestContext, Principals principals) {
        requestContext.setProperty(STOCKS_PRINCIPAL, principals);

        LOG.info(principals.getReadableString() + " " +
                requestContext.getMethod().toLowerCase() + "s " +
                requestContext.getUriInfo().getPath());
    }

    public static Validation<StatusCode, Principals> parseSubjectName(String subject){
        LOG.debug("Parsing " + subject);
        subject = subject.trim();
        Validation<StatusCode, String> rawSubject = extractCommonName(subject);

        if (rawSubject.isFail()) {
            return Validation.fail(rawSubject.fail());
        }

        String commonName = rawSubject.success();

        int[] indices = new int[3];
        int lastIndex = -1;
        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = commonName.indexOf('$', lastIndex+1);
            lastIndex = indices[i];
            if (lastIndex == -1){
                LOG.warn("client name '" + subject + "' is malformed");
                return Validation.fail(StatusCode.INVALID_ARGUMENT);
            }
        }

        return Validation.success(new Principals(commonName.substring(0, indices[0]),
                commonName.substring(indices[1] + 1, indices[2]),
                commonName.substring(indices[0] + 1, indices[1]),
                commonName.substring(indices[2] + 1)));

    }

    private static Validation<StatusCode, String> extractCommonName(String subject) {
        Pattern pattern = Pattern.compile(".*CN=([-_ a-zA-Z0-9$]*).*");
        Matcher matcher = pattern.matcher(subject);
        if (matcher.matches()) {
            return Validation.success(matcher.group(1));
        } else {
            LOG.warn("client name '" + subject + "' is malformed");
            return Validation.fail(StatusCode.INVALID_ARGUMENT);
        }
    }

}
