package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.EanNumber;
import de.njsm.stocks.server.v1.internal.data.EanNumberFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EanEndpointTest extends BaseTestEndpoint {

    private EanNumber testItem;

    private EanEndpoint uut;

    private DatabaseHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new EanEndpoint(handler);

        Mockito.when(handler.get(EanNumberFactory.f))
                .thenReturn(new Data[0]);
        testItem = new EanNumber(1, "123-123-123", 2);
    }

    @Test
    public void testGettingNumbers() {
        Data[] result = uut.getEanNumbers(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(EanNumberFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingNumber() {
        uut.addEanNumber(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        EanNumber data = new EanNumber(3, "123-123-123", 3);
        EanNumber expected = new EanNumber(0, "123-123-123", 3);

        uut.addEanNumber(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }

    @Test
    public void testRemovingNumber() {
        uut.removeEanNumber(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}