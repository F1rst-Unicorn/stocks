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

import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class FoodListRepositoryImplItemAmountTest extends FoodListRepositoryImplTestBase {

    UnitDbEntity unit;
    ScaledUnitDbEntity scaledUnit;

    @Before
    public void setUp() {
        unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
    }

    @Test
    public void itemInDifferentLocationIsIgnored() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id() + 1)
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void singleItemIsReturned() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        testList(actual).assertValue(singletonList(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentFoodAreSeparate() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentFood = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentFood));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentFood.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentUnitAreSeparate() {
        UnitDbEntity unit2 = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit2));
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit2));
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentScaledUnit.ofType(),
                scaledUnit2.id(),
                unit2.id(),
                scaledUnit.scale(),
                unit2.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentScaledUnitAreSeparate() {
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit2));
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentScaledUnit.ofType(),
                scaledUnit2.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfSameScaledUnitAreMerged() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodItem.ofType())
                .storedIn(location.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmountsIn(location::id);

        testList(actual).assertValue(singletonList(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 2)));
    }

    @Test
    public void noItemsGivesEmptyList() {
        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        test(actual).assertValue(emptyList());
    }

    @Test
    public void singleItemOfAnyLocationIsReturned() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        testList(actual).assertValue(singletonList(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentFoodOfAnyLocationAreSeparate() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentFood = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentFood));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentFood.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentUnitOfAnyLocationAreSeparate() {
        UnitDbEntity unit2 = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit2));
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit2));
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentScaledUnit.ofType(),
                scaledUnit2.id(),
                unit2.id(),
                scaledUnit.scale(),
                unit2.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfDifferentScaledUnitOfAnyLocationAreSeparate() {
        ScaledUnitDbEntity scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit2));
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        testList(actual).assertValue(v -> v.size() == 2);
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
        testList(actual).assertValue(v -> v.contains(StoredFoodAmount.create(
                foodItemOfDifferentScaledUnit.ofType(),
                scaledUnit2.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 1)));
    }

    @Test
    public void twoItemsOfSameScaledUnitOfAnyLocationAreMerged() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .build();
        FoodItemDbEntity foodItemOfDifferentScaledUnit = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodItem.ofType())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItemOfDifferentScaledUnit));

        Observable<List<StoredFoodAmount>> actual = uut.getFoodAmounts();

        testList(actual).assertValue(singletonList(StoredFoodAmount.create(
                foodItem.ofType(),
                scaledUnit.id(),
                unit.id(),
                scaledUnit.scale(),
                unit.abbreviation(), 2)));
    }
}