package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class FoodManagerTest {

    private FoodManager uut;

    private FoodHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(FoodHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new FoodManager(backend);
    }

    @After
    public void tearDown() {
        Mockito.verify(backend).commit();
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingItemsIsForwarded() {
        Mockito.when(backend.get()).thenReturn(Validation.success(Collections.emptyList()));

        Validation<StatusCode, List<Food>> result = uut.get();

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get();
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        Food data = new Food(1, "Cheese", 2);
        Mockito.when(backend.add(data)).thenReturn(Validation.success(1));

        Validation<StatusCode, Integer> result = uut.add(data);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).add(data);
    }

    @Test
    public void testRenamingItem() {
        Food data = new Food(1, "Cheese", 2);
        String newName = "Sausage";
        Mockito.when(backend.rename(data, newName)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(data, newName);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).rename(data, newName);
    }

    @Test
    public void testDeletingItem() {
        Food data = new Food(1, "Cheese", 2);
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
    }
}