package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.FoodItemFactory;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import static org.mockito.Matchers.any;

public class FoodItemEndpointTest extends BaseTestEndpoint {

    private FoodItem testItem;

    private FoodEndpoint uut;

    private DatabaseHandler handler;

    private UserContextFactory authAdmin;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        uut = new FoodEndpoint(handler, authAdmin);

        testItem = new FoodItem(1,
                Instant.now(),
                2, 3, 4, 5);
        Mockito.when(handler.get(FoodItemFactory.f))
                .thenReturn(new Data[0]);
        Mockito.when(authAdmin.getPrincipals(any()))
                .thenReturn(testUser);
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
    public void testRemovingFoodItem() {
        uut.removeFoodItem(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}
