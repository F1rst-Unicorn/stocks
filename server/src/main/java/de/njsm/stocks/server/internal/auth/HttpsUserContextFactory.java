package de.njsm.stocks.server.internal.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpsUserContextFactory implements UserContextFactory {

    private static final Logger LOG = LogManager.getLogger(HttpsUserContextFactory.class);

    public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";


    @Override
    public Principals getPrincipals(HttpServletRequest request) {
        String clientName = request.getHeader(SSL_CLIENT_KEY);
        return parseSubjectName(clientName);
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

    Principals parseSubjectName(String subject){
        LOG.debug("Parsing " + subject);
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

    private String extractCommonName(String subject) {
        Pattern pattern = Pattern.compile(".*CN=([a-zA-Z0-9\\$]*).*");
        Matcher matcher = pattern.matcher(subject);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new SecurityException("Client name is malformed");
        }
    }

}
