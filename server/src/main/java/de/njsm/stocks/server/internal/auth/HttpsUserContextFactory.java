package de.njsm.stocks.server.internal.auth;

import javax.servlet.http.HttpServletRequest;

public class HttpsUserContextFactory{

    public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";


    public Principals getPrincipals(HttpServletRequest request) {
        String clientName = request.getHeader(SSL_CLIENT_KEY);
        return parseSubjectName(clientName);
    }

    public boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }

    public Principals parseSubjectName(String subject){
        int[] indices = new int[3];
        int last_index = subject.lastIndexOf("=");
        int start = last_index;

        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = subject.indexOf('$', last_index+1);
            last_index = indices[i];
            if (last_index == -1){
                throw new SecurityException("client name is malformed");
            }
        }

        return new Principals(subject.substring(start + 1, indices[0]),
                subject.substring(indices[1] + 1, indices[2]),
                subject.substring(indices[0] + 1, indices[1]),
                subject.substring(indices[2] + 1, subject.length()));

    }

}
