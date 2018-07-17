package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.UpdateFactory;
import de.njsm.stocks.server.util.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

public class UpdateEndpointTest extends BaseTestEndpoint {

    private UpdateEndpoint uut;

    private DatabaseHandler handler;

    private UserContextFactory authAdmin;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        uut = new UpdateEndpoint(handler, authAdmin);

        Mockito.when(handler.get(UpdateFactory.f))
                .thenReturn(new Data[0]);
        Mockito.when(authAdmin.getPrincipals(any()))
                .thenReturn(TEST_USER);
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
