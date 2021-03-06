/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LocationManagerTest {

    private LocationManager uut;

    private LocationHandler dbLayer;

    private FoodHandler foodHandler;

    private FoodItemHandler foodItemDbLayer;

    @Before
    public void setup() {
        dbLayer = Mockito.mock(LocationHandler.class);
        foodItemDbLayer = Mockito.mock(FoodItemHandler.class);
        foodHandler = Mockito.mock(FoodHandler.class);

        uut = new LocationManager(dbLayer, foodHandler, foodItemDbLayer);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() {
        Mockito.verify(dbLayer).setPrincipals(TEST_USER);
        Mockito.verify(foodItemDbLayer).setPrincipals(TEST_USER);
        Mockito.verify(foodHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(dbLayer);
        Mockito.verifyNoMoreInteractions(foodHandler);
        Mockito.verifyNoMoreInteractions(foodItemDbLayer);
    }

    @Test
    public void puttingIsDelegated() {
        LocationForInsertion input = new LocationForInsertion("test");
        Mockito.when(dbLayer.add(input)).thenReturn(Validation.success(1));
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.put(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).add(input);
        Mockito.verify(dbLayer).commit();
    }

    @Test
    public void gettingIsDelegated() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(dbLayer.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Stream<Location>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        assertEquals(0, result.success().count());
        Mockito.verify(dbLayer).get(false, Instant.EPOCH);
        Mockito.verify(dbLayer).setReadOnly();
    }

    @Test
    public void renamingIsDelegated() {
        String newName = "newName";
        LocationForRenaming input = new LocationForRenaming(1, 2, newName);
        Mockito.when(dbLayer.rename(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).rename(input);
        Mockito.verify(dbLayer).commit();

    }

    @Test
    public void deleteWithoutCascade() {
        LocationForDeletion input = new LocationForDeletion(1, 2);
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodHandler.unregisterDefaultLocation(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input, false);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).delete(input);
        Mockito.verify(dbLayer).commit();
        Mockito.verify(foodHandler).unregisterDefaultLocation(input);
    }

    @Test
    public void deleteWithCascadeSucceeds() {
        LocationForDeletion input = new LocationForDeletion(1, 2);
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodHandler.unregisterDefaultLocation(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input, true);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
        Mockito.verify(dbLayer).delete(input);
        Mockito.verify(dbLayer).commit();
        Mockito.verify(foodHandler).unregisterDefaultLocation(input);
    }

    @Test
    public void deleteWithCascadeFailsWhileDeletingItems() {
        LocationForDeletion input = new LocationForDeletion(1, 2);
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Mockito.when(foodItemDbLayer.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input, true);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
        Mockito.verify(dbLayer).rollback();
    }

    @Test
    public void settingDescriptionIsForwarded() {
        LocationForSetDescription input = new LocationForSetDescription(1, 2, "new description");
        Mockito.when(dbLayer.setDescription(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.setDescription(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).setDescription(input);
        Mockito.verify(dbLayer).commit();
    }
}
