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

package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.data.Data;
import de.njsm.stocks.server.v1.internal.data.Food;
import de.njsm.stocks.server.v1.internal.data.FoodFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FoodEndpointTest extends BaseTestEndpoint {

    private Food testItem;

    private FoodEndpoint uut;

    private DatabaseHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new FoodEndpoint(handler);

        Mockito.when(handler.get(FoodFactory.f))
                .thenReturn(new Data[0]);
        testItem = new Food(1, "Carrot");
    }

    @Test
    public void testGettingFood() {
        Data[] result = uut.getFood(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(FoodFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingFood() {
        uut.addFood(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        Food data = new Food(3, "123-123-123");
        Food expected = new Food(0, "123-123-123");

        uut.addFood(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }

    @Test
    public void testRenamingFood() {
        String newName = "Beer";
        uut.renameFood(createMockRequest(), testItem, newName);

        Mockito.verify(handler).rename(testItem, newName);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testRemovingFood() {
        uut.removeFood(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}
