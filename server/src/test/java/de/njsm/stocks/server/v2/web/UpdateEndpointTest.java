package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UpdateManager;
import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class UpdateEndpointTest {

    private UpdateEndpoint uut;

    private UpdateManager dbLayer;

    @Before
    public void setup() {
        dbLayer = Mockito.mock(UpdateManager.class);
        uut = new UpdateEndpoint(dbLayer);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbLayer);
    }

    @Test
    public void getUpdates() {
        Mockito.when(dbLayer.getUpdates())
                .thenReturn(Validation.success(new LinkedList<>()));

        ListResponse<Update> result = uut.getUpdates();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(0, result.data.size());
        Mockito.verify(dbLayer).getUpdates();
    }


}