package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import fj.data.Validation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;

public class FoodEndpointTest extends BaseTestEndpoint {

    private Food testItem;

    private FoodEndpoint uut;

    private FoodHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(FoodHandler.class);
        uut = new FoodEndpoint(handler);

        Mockito.when(handler.get())
                .thenReturn(Validation.success(new LinkedList<>()));
        testItem = new Food(1, "Carrot", 1);
    }

    @Test
    public void testGettingFood() {
        Food[] result = uut.getFood(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get();
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingFood() {
        uut.addFood(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        Food data = new Food(3, "123-123-123", 1);
        Food expected = new Food(0, "123-123-123", 1);

        uut.addFood(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }

    @Test
    public void testRenamingFood() {
        String newName = "Beer";
        uut.renameFood(createMockRequest(), testItem, newName);

        Mockito.verify(handler).rename(testItem, newName);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testRemovingFood() {
        uut.removeFood(createMockRequest(), testItem);

        Mockito.verify(handler).delete(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}
