package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.UpdateFactory;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateEndpointTest extends BaseTestEndpoint {

    private Config c;

    private UpdateEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(UpdateFactory.f))
                .thenReturn(new Data[0]);

        uut = new UpdateEndpoint(c);
    }

    @Test
    public void testGettingUpdates() {
        Data[] result = uut.getUpdates(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(UpdateFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}
