package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class FoodItemManagerTest {

    private FoodItemManager uut;

    private FoodItemHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(FoodItemHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new FoodItemManager(backend);
    }

    @After
    public void tearDown() {
        Mockito.verify(backend).commit();
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingItemsIsForwarded() {
        Mockito.when(backend.get()).thenReturn(Validation.success(Collections.emptyList()));

        Validation<StatusCode, List<FoodItem>> result = uut.get();

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get();
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        FoodItem data = new FoodItem(1, 2, Instant.now(), 2, 2, 3, 3);
        Mockito.when(backend.add(data)).thenReturn(Validation.success(1));

        Validation<StatusCode, Integer> result = uut.add(data);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).add(data);
    }

    @Test
    public void testRenamingItem() {
        FoodItem data = new FoodItem(1, 2, Instant.now(), 2, 2, 3, 3);
        Mockito.when(backend.edit(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).edit(data);
    }

    @Test
    public void testDeletingItem() {
        FoodItem data = new FoodItem(1, 2, Instant.now(), 2, 2, 3, 3);
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
    }
}