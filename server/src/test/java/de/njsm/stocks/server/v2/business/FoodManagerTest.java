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

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import java.time.Period;
import java.util.stream.Stream;

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
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void gettingItemsIsForwarded() {
        AsyncResponse r = Mockito.mock(AsyncResponse.class);
        Mockito.when(backend.get(false)).thenReturn(Validation.success(Stream.empty()));

        Validation<StatusCode, Stream<Food>> result = uut.get(r, false);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).get(false);
        Mockito.verify(backend).setReadOnly();
    }

    @Test
    public void testAddingItem() {
        Food data = new Food(1, "Cheese", 2, true, Period.ZERO, 1);
        Mockito.when(backend.add(data)).thenReturn(Validation.success(1));

        Validation<StatusCode, Integer> result = uut.add(data);

        assertTrue(result.isSuccess());
        Mockito.verify(backend).add(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testRenamingItem() {
        String newName = "Sausage";
        Food data = new Food(1, newName, 2, true, Period.ZERO, 1);
        Mockito.when(backend.edit(data, newName, Period.ZERO, 1)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).edit(data, newName, Period.ZERO, 1);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testSettingBuyStatusItem() {
        String newName = "Sausage";
        Food data = new Food(1, newName, 2, true, Period.ZERO, 1);
        Mockito.when(backend.setToBuyStatus(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.setToBuyStatus(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).setToBuyStatus(data);
        Mockito.verify(backend).commit();
    }

    @Test
    public void testDeletingItem() {
        Food data = new Food(1, "Cheese", 2, true, Period.ZERO, 1);
        Mockito.when(backend.delete(data)).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).delete(data);
        Mockito.verify(backend).commit();
    }
}
