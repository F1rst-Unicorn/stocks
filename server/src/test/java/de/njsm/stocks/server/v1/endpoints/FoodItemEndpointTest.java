package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.FoodItemFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

public class FoodItemEndpointTest extends BaseTestEndpoint {

    private FoodItem testItem;

    private FoodEndpoint uut;

    private DatabaseHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new FoodEndpoint(handler);

        testItem = new FoodItem(1,
                Instant.now(),
                2, 3, 4, 5);
        Mockito.when(handler.get(FoodItemFactory.f))
                .thenReturn(new Data[0]);
    }

    @Test
    public void testGettingFoodItems() {
        Data[] result = uut.getFoodItems(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(FoodItemFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingFoodItem() {
        uut.addFoodItem(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        FoodItem data = new FoodItem(3, Instant.EPOCH, 0, 0, 0, 0);
        FoodItem expected = new FoodItem(0, Instant.EPOCH, 0, 0, 1, 5);

        uut.addFoodItem(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }

    @Test
    public void testRemovingFoodItem() {
        uut.removeFoodItem(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testMovingItem() {
        uut.moveFoodItem(createMockRequest(), testItem, 2);

        Mockito.verify(handler).moveItem(testItem, 2);
        Mockito.verifyNoMoreInteractions(handler);
    }
}
