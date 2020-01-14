package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.HealthManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Health;
import de.njsm.stocks.server.v2.web.data.DataResponse;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthEndpointTest {

    private HealthEndpoint uut;

    private HealthManager businessMock;

    @Before
    public void setup() {
        businessMock = mock(HealthManager.class);
        uut = new HealthEndpoint(businessMock);
    }

    @Test
    public void fineHealthIsReported() {
        when(businessMock.get()).thenReturn(Validation.success(new Health(true, true)));

        DataResponse<Health> output = uut.getStatus();

        assertEquals(StatusCode.SUCCESS, output.status);
        assertTrue(output.data.database);
        assertTrue(output.data.ca);
    }

    @Test
    public void failingHealthIsReported() {
        when(businessMock.get()).thenReturn(Validation.fail(StatusCode.GENERAL_ERROR));

        DataResponse<Health> output = uut.getStatus();

        assertEquals(StatusCode.GENERAL_ERROR, output.status);
        assertNull(output.data);
    }
}