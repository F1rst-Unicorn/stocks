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
import de.njsm.stocks.client.business.entities.FoodWithAmountForListingBaseData;
import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.testList;
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

    @Test
    public void gettingFoodToBuyWorks() {
        var foodToBuy = standardEntities.foodDbEntityBuilder()
                .toBuy(true)
                .build();
        stocksDatabase.synchronisationDao().synchroniseFood(singletonList(foodToBuy));

        var actual = uut.getFoodToBuy();

        testList(actual).assertValue(singletonList(FoodWithAmountForListingBaseData.create(
                foodToBuy.id(),
                foodToBuy.name()
        )));
    }

    @Test
    public void gettingFoodAmountsOfFoodToBuyWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foodToBuy = standardEntities.foodDbEntityBuilder()
                .toBuy(true)
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().synchroniseFood(singletonList(foodToBuy));
        var foundFoodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodToBuy.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foundFoodItem));

        var actual = uut.getFoodAmountsToBuy();

        testList(actual).assertValue(List.of(
                StoredFoodAmount.create(
                        foodToBuy.id(),
                        scaledUnit.id(),
                        unit.id(),
                        scaledUnit.scale(),
                        unit.abbreviation(),
                        1
                )
        ));
    }

    @Test
    public void gettingFoodUnitsOfAbsentFoodToBuyWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foodToBuy = standardEntities.foodDbEntityBuilder()
                .toBuy(true)
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().synchroniseFood(singletonList(foodToBuy));

        var actual = uut.getFoodDefaultUnitOfFoodWithoutItems();

        testList(actual).assertValue(List.of(
                StoredFoodAmount.create(
                        foodToBuy.id(),
                        scaledUnit.id(),
                        unit.id(),
                        scaledUnit.scale(),
                        unit.abbreviation(),
                        0
                )
        ));
    }
}