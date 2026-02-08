/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class LocationManagerTest {

    private LocationManager uut;

    private LocationHandler dbLayer;

    private FoodHandler foodHandler;

    private FoodItemHandler foodItemDbLayer;

    @BeforeEach
    public void setup() {
        dbLayer = Mockito.mock(LocationHandler.class);
        foodItemDbLayer = Mockito.mock(FoodItemHandler.class);
        foodHandler = Mockito.mock(FoodHandler.class);

        uut = new LocationManager(dbLayer, foodHandler, foodItemDbLayer);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(dbLayer);
        Mockito.verifyNoMoreInteractions(foodHandler);
        Mockito.verifyNoMoreInteractions(foodItemDbLayer);
    }

    @Test
    public void puttingIsDelegated() {
        LocationForInsertion input = LocationForInsertion.builder()
                .name("test")
                .build();
        Mockito.when(dbLayer.add(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.put(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).add(input);
        Mockito.verify(dbLayer).commit();
    }

    @Test
    public void gettingIsDelegated() {
        Mockito.when(dbLayer.get(Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(emptyList()));
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);
        when(dbLayer.setReadOnly()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, List<Location>> result = uut.get(Instant.EPOCH, Instant.EPOCH);

        assertTrue(result.isSuccess());
        assertEquals(0, result.success().size());
        Mockito.verify(dbLayer).get(Instant.EPOCH, Instant.EPOCH);
        Mockito.verify(dbLayer).setReadOnly();
        Mockito.verify(dbLayer).commit();
    }

    @Test
    public void renamingIsDelegated() {
        LocationForRenaming input = LocationForRenaming.builder()
                .id(1)
                .version(2)
                .name("new name")
                .build();
        Mockito.when(dbLayer.rename(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).rename(input);
        Mockito.verify(dbLayer).commit();

    }

    @Test
    public void editingIsDelegated() {
        LocationForEditing input = LocationForEditing.builder()
                .id(1)
                .version(2)
                .name("new name")
                .description("new description")
                .build();
        Mockito.when(dbLayer.edit(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).edit(input);
        Mockito.verify(dbLayer).commit();

    }

    @Test
    public void deleteWithoutCascade() {
        LocationForDeletion input = LocationForDeletion.builder()
                .id(1)
                .version(2)
                .cascade(false)
                .build();
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodHandler.unregisterDefaultLocation(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).delete(input);
        Mockito.verify(dbLayer).commit();
        Mockito.verify(foodHandler).unregisterDefaultLocation(input);
    }

    @Test
    public void deleteWithCascadeSucceeds() {
        LocationForDeletion input = LocationForDeletion.builder()
                .id(1)
                .version(2)
                .cascade(true)
                .build();
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.delete(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodHandler.unregisterDefaultLocation(input)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
        Mockito.verify(dbLayer).delete(input);
        Mockito.verify(dbLayer).commit();
        Mockito.verify(foodHandler).unregisterDefaultLocation(input);
    }

    @Test
    public void deleteWithCascadeFailsWhileDeletingItems() {
        LocationForDeletion input = LocationForDeletion.builder()
                .id(1)
                .version(2)
                .cascade(true)
                .build();
        Mockito.when(foodItemDbLayer.deleteItemsStoredIn(input)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        Mockito.when(foodItemDbLayer.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(foodItemDbLayer).deleteItemsStoredIn(input);
        Mockito.verify(dbLayer).rollback();
    }

    @Test
    public void settingDescriptionIsForwarded() {
        LocationForSetDescription input = LocationForSetDescription.builder()
                .id(1)
                .version(2)
                .description("new description")
                .build();
        Mockito.when(dbLayer.setDescription(input)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(dbLayer.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.setDescription(input);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(dbLayer).setDescription(input);
        Mockito.verify(dbLayer).commit();
    }
}
