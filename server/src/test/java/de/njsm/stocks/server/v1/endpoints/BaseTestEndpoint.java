package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.business.HttpsUserContextFactory;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class BaseTestEndpoint {

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static final Principals TEST_USER = new Principals("John", "Mobile", 5, 1);

    public static HttpServletRequest createMockRequest() {
        HttpServletRequest result = Mockito.mock(HttpServletRequest.class);

        Mockito.when(result.getHeader(HttpsUserContextFactory.SSL_CLIENT_KEY))
                .thenReturn(USER_STRING);

        return result;
    }
}
