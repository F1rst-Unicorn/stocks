package de.njsm.stocks.server.v1.internal.auth;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.util.HttpsUserContextFactory;
import de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpsUserContextFactoryTest {

    @Test
    public void testFullCall() {
        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals output = uut.getPrincipals(BaseTestEndpoint.createMockRequest());

        assertEquals(BaseTestEndpoint.TEST_USER, output);
    }
}
