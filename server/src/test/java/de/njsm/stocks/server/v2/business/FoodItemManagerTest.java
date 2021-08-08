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


import de.njsm.stocks.common.api.FoodItem;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.FoodItemForDeletion;
import de.njsm.stocks.common.api.FoodItemForEditing;
import de.njsm.stocks.common.api.FoodItemForInsertion;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class FoodItemManagerTest {

    private FoodItemManager uut;

    private FoodItemHandler backend;

    private FoodHandler foodHandler;

    @BeforeEach
    public void setup() {
        backend = Mockito.mock(FoodItemHandler.class);
        foodHandler = Mockito.mock(FoodHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new FoodItemManager(backend, foodHandler);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verify(backend).setPrincipals(TEST_USER);
        Mockito.verify(foodHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(backend);
        Mockito.verifyNoMoreInteractions(foodHandler);
    }

    @Test
    public void gettingItemsIsForwarded() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));

        Validation<StatusCode, Stream<FoodItem>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(false, Instant.EPOCH);
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        FoodItemForInsertion data = new FoodItemForInsertion(Instant.now(), 2, 2, 3, 3, 1);
        Mockito.when(backend.add(data)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodHandler.setToBuyStatus(any(), eq(false))).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(data);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).add(data);
        Mockito.verify(backend).commit();
        Mockito.verify(foodHandler).setToBuyStatus(any(), eq(false));
    }

    @Test
    public void testRenamingItem() {
        FoodItemForEditing data = new FoodItemForEditing(1, 2, Instant.now(), 3, 1);
        Mockito.when(backend.edit(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).edit(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        FoodItemForDeletion data = new FoodItemForDeletion(1, 2);
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
    }
}
