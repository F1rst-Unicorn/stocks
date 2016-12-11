package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.Data;
import de.njsm.stocks.server.data.EanNumber;
import de.njsm.stocks.server.data.EanNumberFactory;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class EanEndpointTest extends BaseEndpointTest {

    private Config c;
    private EanNumber testItem;

    private EanEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(EanNumberFactory.f))
                .thenReturn(new Data[0]);
        testItem = new EanNumber(1, "123-123-123", 2);

        uut = new EanEndpoint(c);
    }

    @Test
    public void testGettingNumbers() {
        Data[] result = uut.getEanNumbers(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(EanNumberFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingNumber() {
        uut.addEanNumber(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).add(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingNumber() {
        uut.removeEanNumber(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).remove(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}
