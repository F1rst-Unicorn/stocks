package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import de.njsm.stocks.server.v2.web.PrincipalFilterTest;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class BaseTestEndpoint {

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static HttpServletRequest createMockRequest() {
        HttpServletRequest result = Mockito.mock(HttpServletRequest.class);

        Mockito.when(result.getHeader(PrincipalFilter.SSL_CLIENT_KEY))
                .thenReturn(USER_STRING);
        Mockito.when(result.getAttribute(PrincipalFilter.STOCKS_PRINCIPAL))
                .thenReturn(PrincipalFilterTest.TEST_USER);

        return result;
    }
}
