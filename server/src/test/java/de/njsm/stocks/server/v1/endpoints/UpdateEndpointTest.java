package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.UpdateFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateEndpointTest extends BaseTestEndpoint {

    private UpdateEndpoint uut;

    private DatabaseHandler handler;


    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new UpdateEndpoint(handler);

        Mockito.when(handler.get(UpdateFactory.f))
                .thenReturn(new Data[0]);
    }
    
    @Test
    public void testGettingUpdates() {
        Data[] result = uut.getUpdates(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(UpdateFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

}
