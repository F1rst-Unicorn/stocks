package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import de.njsm.stocks.server.v2.web.PrincipalFilterTest;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.USER_STRING;

public class BaseTestEndpoint {

    public static HttpServletRequest createMockRequest() {
        HttpServletRequest result = Mockito.mock(HttpServletRequest.class);

        Mockito.when(result.getHeader(PrincipalFilter.SSL_CLIENT_KEY))
                .thenReturn(PrincipalFilterTest.USER_STRING);
        Mockito.when(result.getAttribute(PrincipalFilter.STOCKS_PRINCIPAL))
                .thenReturn(PrincipalFilterTest.TEST_USER);

        return result;
    }
}
