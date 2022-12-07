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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.FoodToBuyRepository;
import de.njsm.stocks.client.business.entities.FoodForBuying;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class FoodToBuyRepositoryImplTest extends DbTestCase {

    private FoodDbEntity food;

    private FoodToBuyRepository uut;

    @Before
    public void setUp() {
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        uut = new FoodToBuyRepositoryImpl(stocksDatabase.foodDao());
    }

    @Test
    public void foodWithoutItemInLocationIsNotReturned() {
        var actual = uut.getCurrentFood(food::id);

        assertEquals(actual, FoodForBuying.create(
                food.id(),
                food.version(),
                food.toBuy()
        ));
    }
}