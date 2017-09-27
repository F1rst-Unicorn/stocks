package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.FoodItemFactory;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;

public class FoodItemEndpointTest extends BaseTestEndpoint {

    private Config c;
    private FoodItem testItem;

    private FoodEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(FoodItemFactory.f))
                .thenReturn(new Data[0]);
        testItem = new FoodItem(1,
                Instant.now(),
                2, 3, 4, 5);

        uut = new FoodEndpoint(c);
    }

    @Test
    public void testGettingFoodItems() {
        Data[] result = uut.getFoodItems(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(FoodItemFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingFoodItem() {
        uut.addFoodItem(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).add(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingFoodItem() {
        uut.removeFoodItem(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).remove(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}
