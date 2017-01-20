package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FoodEndpointTest extends BaseTestEndpoint {

    private Config c;
    private Food testItem;

    private FoodEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(FoodFactory.f))
                .thenReturn(new Data[0]);
        testItem = new Food(1, "Carrot");

        uut = new FoodEndpoint(c);
    }

    @Test
    public void testGettingFood() {
        Data[] result = uut.getFood(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(FoodFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingFood() {
        uut.addFood(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).add(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRenamingFood() {
        String newName = "Beer";
        uut.renameFood(createMockRequest(), testItem, newName);

        Mockito.verify(c.getDbHandler()).rename(testItem, newName);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingFood() {
        uut.removeFood(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).remove(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}
