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


import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
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

public class FoodManagerTest {

    private FoodManager uut;

    private FoodHandler backend;

    private FoodItemHandler foodItemHandler;

    private EanNumberHandler eanNumberHandler;

    @BeforeEach
    public void setup() {
        backend = Mockito.mock(FoodHandler.class);
        foodItemHandler = Mockito.mock(FoodItemHandler.class);
        eanNumberHandler = Mockito.mock(EanNumberHandler.class);
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);
        uut = new FoodManager(backend, foodItemHandler, eanNumberHandler);
        uut.setPrincipals(TEST_USER);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verify(backend).setPrincipals(TEST_USER);
        Mockito.verify(foodItemHandler).setPrincipals(TEST_USER);
        Mockito.verify(eanNumberHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(backend);
        Mockito.verifyNoMoreInteractions(foodItemHandler);
        Mockito.verifyNoMoreInteractions(eanNumberHandler);
    }

    @Test
    public void gettingItemsIsForwarded() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get(false, Instant.EPOCH)).thenReturn(Validation.success(Stream.empty()));

        Validation<StatusCode, Stream<Food>> result = uut.get(r, false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(false, Instant.EPOCH);
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        FoodForInsertion data = FoodForInsertion.builder()
                .name("Cheese")
                .storeUnit(1)
                .build();
        Mockito.when(backend.add(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).add(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testRenamingItem() {
        FoodForEditing data = FoodForEditing.builder()
                .id(1)
                .version(2)
                .name("Sausage")
                .expirationOffset(0)
                .location(1)
                .description("new description")
                .storeUnit(1)
                .build();
        FoodForFullEditing expected = FoodForFullEditing.builder()
                .id(1)
                .version(2)
                .name("Sausage")
                .toBuy(null)
                .expirationOffset(0)
                .location(1)
                .description("new description")
                .storeUnit(1)
                .build();
        Mockito.when(backend.edit(expected)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).edit(expected);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testSettingBuyStatusItem() {
        FoodForSetToBuy data = FoodForSetToBuy.builder()
                .id(1)
                .version(2)
                .toBuy(true)
                .build();
        Mockito.when(backend.setToBuyStatus(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).setToBuyStatus(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        FoodForDeletion data = FoodForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(foodItemHandler.deleteItemsOfType(data)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(eanNumberHandler.deleteOwnedByFood(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
        Mockito.verify(foodItemHandler).deleteItemsOfType(data);
        Mockito.verify(eanNumberHandler).deleteOwnedByFood(data);
    }

    @Test
    public void settingDescriptionWorks() {
        FoodForSetDescription data = FoodForSetDescription.builder()
                .id(1)
                .version(2)
                .description("some description")
                .build();
        Mockito.when(backend.setDescription(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.setDescription(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).setDescription(data);
        Mockito.verify(backend).commit();
    }
}
