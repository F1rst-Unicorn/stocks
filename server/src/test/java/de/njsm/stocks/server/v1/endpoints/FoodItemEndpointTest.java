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
import de.njsm.stocks.server.v1.internal.data.FoodItem;
import de.njsm.stocks.server.v1.internal.data.FoodItemFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.Instant;

public class FoodItemEndpointTest extends BaseTestEndpoint {

    private FoodItem testItem;

    private FoodItemEndpoint uut;

    private DatabaseHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new FoodItemEndpoint(handler);

        testItem = new FoodItem(1,
                Instant.now(),
                2, 3, 4, 5);
        Mockito.when(handler.get(FoodItemFactory.f))
                .thenReturn(new Data[0]);
    }

    @Test
    public void testGettingFoodItems() {
        Data[] result = uut.getFoodItems(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(FoodItemFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingFoodItem() {
        uut.addFoodItem(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        FoodItem data = new FoodItem(3, Instant.EPOCH, 0, 0, 0, 0);
        FoodItem expected = new FoodItem(0, Instant.EPOCH, 0, 0, 1, 5);

        uut.addFoodItem(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }

    @Test
    public void testRemovingFoodItem() {
        uut.removeFoodItem(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testMovingItem() {
        uut.moveFoodItem(createMockRequest(), testItem, 2);

        Mockito.verify(handler).moveItem(testItem, 2);
        Mockito.verifyNoMoreInteractions(handler);
    }
}
