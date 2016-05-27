package de.njsm.stocks.internal.auth;

import javax.servlet.http.HttpServletRequest;

public class HttpsUserContextFactory implements ContextFactory {


    public Principals getPrincipals(HttpServletRequest request) {

        String clientName = request.getHeader("X-SSL-Client-S-DN");
        return parseSubjectName(clientName);
    }

    public Principals parseSubjectName(String subject){
        int[] indices = new int[3];
        int last_index = subject.lastIndexOf("=");

        // find indices of the $ signs
        for (int i = 0; i < 3; i++){
            indices[i] = subject.indexOf('$', last_index+1);
            last_index = indices[i];
            if (last_index == -1){
                throw new SecurityException("client name is malformed");
            }
        }

        return new Principals(subject.substring(0, indices[0]),
                subject.substring(indices[1] + 1, indices[2]),
                subject.substring(indices[0] + 1, indices[1]),
                subject.substring(indices[2] + 1, subject.length()));

    }

}
