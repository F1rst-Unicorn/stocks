package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class PrincipalFilter implements ContainerRequestFilter {

    private static final Logger LOG = LogManager.getLogger(PrincipalFilter.class);

    public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";

    public static final String ORIGIN = "X-ORIGIN";

    public static final String STOCKS_PRINCIPAL = "de.njsm.stocks.server.util.Principals";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (! requestContext.getHeaderString(ORIGIN).equals("sentry")) {
            addPrincipals(requestContext);
        }
    }

    private void addPrincipals(ContainerRequestContext requestContext) {
        Principals principals = parseSubjectName(requestContext.getHeaderString(SSL_CLIENT_KEY));
        requestContext.setProperty(STOCKS_PRINCIPAL, principals);

        LOG.info(principals.getReadableString() + " " + requestContext.getMethod() + "s " + requestContext.getRequest());
    }

    public static Principals parseSubjectName(String subject){
        LOG.debug("Parsing " + subject);
        subject = subject.trim();
        String commonName = extractCommonName(subject);

        int[] indices = new int[3];
        int lastIndex = -1;
        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = commonName.indexOf('$', lastIndex+1);
            lastIndex = indices[i];
            if (lastIndex == -1){
                throw new SecurityException("client name is malformed");
            }
        }

        return new Principals(commonName.substring(0, indices[0]),
                commonName.substring(indices[1] + 1, indices[2]),
                commonName.substring(indices[0] + 1, indices[1]),
                commonName.substring(indices[2] + 1, commonName.length()));

    }

    private static String extractCommonName(String subject) {
        Pattern pattern = Pattern.compile(".*CN=([-_ a-zA-Z0-9\\$]*).*");
        Matcher matcher = pattern.matcher(subject);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new SecurityException("Client name is malformed");
        }
    }

}
