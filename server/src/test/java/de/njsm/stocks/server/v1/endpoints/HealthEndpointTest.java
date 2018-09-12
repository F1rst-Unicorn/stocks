package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v2.web.HealthEndpoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HealthEndpointTest {

    private HealthEndpoint uut;

    @Before
    public void setup() {
        uut = new HealthEndpoint();
    }

    @Test
    public void testSuccess() {
        assertEquals("System is healthy\n\n", uut.getStatus());
    }

}
