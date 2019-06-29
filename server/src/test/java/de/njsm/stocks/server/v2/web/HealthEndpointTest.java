package de.njsm.stocks.server.v2.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class HealthEndpointTest {

    private HealthEndpoint uut;

    @Before
    public void setup() {
        uut = new HealthEndpoint();
    }

    @Test
    public void healthIsReported() {
        assertNotNull(uut.getStatus());
    }
}