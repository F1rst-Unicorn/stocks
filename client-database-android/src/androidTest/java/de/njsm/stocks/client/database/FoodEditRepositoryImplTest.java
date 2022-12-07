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

import de.njsm.stocks.client.business.entities.FoodForEditing;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.database.util.Util;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class FoodEditRepositoryImplTest extends DbTestCase {

    private FoodEditRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new FoodEditRepositoryImpl(stocksDatabase.foodDao(),
                new ScaledUnitRepositoryImpl(stocksDatabase.scaledUnitDao()),
                new LocationRepositoryImpl(stocksDatabase.locationDao()));
    }

    @Test
    public void gettingFoodWorks() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Observable<FoodToEdit> actual = uut.getFood(food::id);

        Util.test(actual).assertValue(FoodToEdit.create(
                food.id(),
                food.name(),
                food.toBuy(),
                food.expirationOffset(),
                food.location(),
                food.storeUnit(),
                food.description()
        ));
    }

    @Test
    public void gettingFoodForSendingWorks() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        FoodForEditing actual = uut.getFoodForSending(food::id);

        assertEquals(actual, FoodForEditing.create(
                food.id(),
                food.version(),
                food.name(),
                food.toBuy(),
                food.expirationOffset(),
                Optional.ofNullable(food.location()),
                food.storeUnit(),
                food.description()
        ));
    }
}