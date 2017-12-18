package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class BaseTestEndpoint {

    protected String userString = "/CN=John$5$Mobile$1";

    protected Principals testUser = new Principals("John", "Mobile", 5, 1);

    protected HttpServletRequest createMockRequest() {
        HttpServletRequest result = Mockito.mock(HttpServletRequest.class);

        Mockito.when(result.getHeader(HttpsUserContextFactory.SSL_CLIENT_KEY))
                .thenReturn(userString);

        return result;
    }
}
