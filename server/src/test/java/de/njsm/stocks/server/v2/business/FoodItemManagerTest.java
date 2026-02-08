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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class FoodItemManagerTest implements AuthenticationSetter {

    private FoodItemManager uut;

    private FoodItemHandler backend;

    private FoodHandler foodHandler;

    @BeforeEach
    public void setup() {
        backend = Mockito.mock(FoodItemHandler.class);
        foodHandler = Mockito.mock(FoodHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new FoodItemManager(backend, foodHandler);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(backend);
        Mockito.verifyNoMoreInteractions(foodHandler);
    }

    @Test
    public void gettingItemsIsForwarded() {
        when(backend.setReadOnly()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(backend.get(Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(emptyList()));

        Validation<StatusCode, List<FoodItem>> result = uut.get(Instant.EPOCH, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(Instant.EPOCH, Instant.EPOCH);
        Mockito.verify(backend).setReadOnly();
        Mockito.verify(backend).commit();
    }

    @Test
    public void testAddingItem() {
        FoodItemForInsertion data = FoodItemForInsertion.builder()
                .eatByDate(Instant.EPOCH)
                .ofType(2)
                .storedIn(2)
                .registers(3)
                .buys(3)
                .unit(1)
                .build();
        Mockito.when(backend.addReturningId(data)).thenReturn(Validation.success(1));
        Mockito.when(foodHandler.setToBuyStatus(any(), eq(false))).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Integer> result = uut.add(data);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).addReturningId(data);
        Mockito.verify(backend).commit();
        Mockito.verify(foodHandler).setToBuyStatus(any(), eq(false));
    }

    @Test
    public void testRenamingItem() {
        FoodItemForEditing data = FoodItemForEditing.builder()
                .id(1)
                .version(2)
                .eatBy(Instant.EPOCH)
                .storedIn(3)
                .unit(1)
                .build();
        Mockito.when(backend.edit(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).edit(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        FoodItemForDeletion data = FoodItemForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
    }
}
