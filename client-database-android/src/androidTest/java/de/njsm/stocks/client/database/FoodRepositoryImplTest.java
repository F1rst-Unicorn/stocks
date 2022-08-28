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

import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Versionable;
import de.njsm.stocks.client.database.util.Util;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class FoodRepositoryImplTest extends DbTestCase {

    private FoodRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new FoodRepositoryImpl(stocksDatabase.foodDao());
    }

    @Test
    public void gettingWorks() {
        FoodDbEntity entity = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(entity));

        Observable<List<EmptyFood>> output = uut.get();

        Util.testList(output).assertValue(singletonList(EmptyFood.create(entity.id(), entity.name(), entity.toBuy())));
    }

    @Test
    public void gettingForDeletionWorks() {
        FoodDbEntity entity = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(entity));

        Versionable<Food> output = uut.getEntityForDeletion(entity::id);

        assertEquals(entity.id(), output.id());
        assertEquals(entity.version(), output.version());
    }
}