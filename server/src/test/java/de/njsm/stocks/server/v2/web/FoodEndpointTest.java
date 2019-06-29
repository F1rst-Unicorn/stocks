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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.FoodManager;
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.server.v2.business.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FoodEndpointTest {

    private FoodEndpoint uut;

    private FoodManager manager;

    @Before
    public void setup() {
        manager = Mockito.mock(FoodManager.class);
        uut = new FoodEndpoint(manager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingNullCodeIsInvalid() {

        Response result = uut.putFood(null);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putFood("");

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidIdIsInvalid() {

        Response result = uut.renameFood(0, 1, "fdsa", 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingInvalidVersionIsInvalid() {

        Response result = uut.renameFood(1, -1, "fdsa", 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void renamingToInvalidNameIsInvalid() {

        Response result = uut.renameFood(1, 1, "", 0);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.deleteFood(0, 1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.deleteFood(1, -1);

        assertEquals(INVALID_ARGUMENT, result.status);
    }

    @Test
    public void foodIsAdded() {
        Food data = new Food(0, "Banana", 0, false);
        when(manager.add(data)).thenReturn(Validation.success(5));

        Response response = uut.putFood(data.name);

        assertEquals(SUCCESS, response.status);
        verify(manager).add(data);
    }

    @Test
    public void getFoodReturnsList() {
        List<Food> data = Collections.singletonList(new Food(2, "Banana", 2, true));
        when(manager.get()).thenReturn(Validation.success(data));

        ListResponse<Food> response = uut.getFood();

        assertEquals(SUCCESS, response.status);
        assertEquals(data, response.data);
        verify(manager).get();
    }

    @Test
    public void renameFoodWorks() {
        String newName = "Bread";
        Food data = new Food(1, newName, 2, true);
        when(manager.edit(data)).thenReturn(SUCCESS);

        Response response = uut.renameFood(data.id, data.version, newName, 1);

        assertEquals(SUCCESS, response.status);
        verify(manager).edit(data);
    }

    @Test
    public void deleteFoodWorks() {
        Food data = new Food(1, "", 2, false);
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteFood(data.id, data.version);

        assertEquals(SUCCESS, response.status);
        verify(manager).delete(data);
    }
}