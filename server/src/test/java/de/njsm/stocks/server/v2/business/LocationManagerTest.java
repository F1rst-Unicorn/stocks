package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LocationManagerTest {

    private LocationManager uut;

    private LocationHandler dbLayer;

    private FoodItemHandler foodItemDbLayer;

    @Before
    public void setup() {
        dbLayer = Mockito.mock(LocationHandler.class);
        foodItemDbLayer = Mockito.mock(FoodItemHandler.class);

        uut = new LocationManager(dbLayer, foodItemDbLayer);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbLayer);
        Mockito.verifyNoMoreInteractions(foodItemDbLayer);
    }

    @Test
    public void puttingIsDelegated() {
        Location input = new Location(1, "test", 2);
        Mockito.when(dbLayer.add(input)).thenReturn(Validation.success(1));

        StatusCode result = uut.put(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).add(input);
    }

    @Test
    public void gettingIsDelegated() {
        Mockito.when(dbLayer.get()).thenReturn(Validation.success(Collections.emptyList()));

        Validation<StatusCode, List<Location>> result = uut.get();

        assertTrue(result.isSuccess());
        assertEquals(0, result.success().size());
        Mockito.verify(dbLayer).get();

    }

    @Test
    public void renamingIsDelegated() {
        Location input = new Location(1, "test", 2);
        String newName = "newName";
        Mockito.when(dbLayer.rename(input, newName)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(input, newName);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).rename(input, newName);

    }

    @Test
    public void deleteWithoutCascade() {
        Location input = new Location(1, "test", 2);
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input, false);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).delete(input);
    }

    @Test
    public void deleteWithCascadeSucceeds() {
        Location input = new Location(1, "test", 2);
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input, true);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
        Mockito.verify(dbLayer).delete(input);
    }

    @Test
    public void deleteWithCascadeFailsWhileDeletingItems() {
        Location input = new Location(1, "test", 2);
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.delete(input, true);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
    }
}