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

import de.njsm.stocks.client.business.entities.FoodForListingBaseData;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class FoodListRepositoryImplTest extends FoodListRepositoryImplTestBase {

    private FoodDbEntity food;

    @Before
    public void setUp() {
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
    }

    @Test
    public void foodWithoutItemInLocationIsNotReturned() {
        Observable<List<FoodForListingBaseData>> actual = uut.getFoodBy(location::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void foodWithSingleItemInLocationIsReturned() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));

        Observable<List<FoodForListingBaseData>> actual = uut.getFoodBy(location::id);

        testList(actual).assertValue(singletonList(FoodForListingBaseData.create(food.id(), food.name(), food.toBuy(), foodItem.eatBy())));
    }

    @Test
    public void foodWithTwoItemsInLocationReturnsLeastEatBy() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .eatBy(Instant.EPOCH.plusSeconds(4))
                .build();
        FoodItemDbEntity foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .eatBy(Instant.EPOCH.plusSeconds(3))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItem2));

        Observable<List<FoodForListingBaseData>> actual = uut.getFoodBy(location::id);

        testList(actual).assertValue(singletonList(FoodForListingBaseData.create(food.id(), food.name(), food.toBuy(), foodItem2.eatBy())));
    }

    @Test
    public void foodItemInDifferentLocationDoesntAffectEatByDate() {
        LocationDbEntity differentLocation = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(differentLocation));
        FoodItemDbEntity foodItemInDifferentLocation = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(differentLocation.id())
                .eatBy(Instant.EPOCH.plusSeconds(2))
                .build();
        FoodItemDbEntity foodItemAffectingResultEatBy = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .eatBy(Instant.EPOCH.plusSeconds(3))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItemInDifferentLocation, foodItemAffectingResultEatBy));

        Observable<List<FoodForListingBaseData>> actual = uut.getFoodBy(location::id);

        testList(actual).assertValue(singletonList(FoodForListingBaseData.create(food.id(), food.name(), food.toBuy(), foodItemAffectingResultEatBy.eatBy())));
    }

    @Test
    public void foodWithoutItemInAnyLocationIsNotReturned() {
        Observable<List<FoodForListingBaseData>> actual = uut.getFood();

        test(actual).assertValue(emptyList());
    }

    @Test
    public void foodWithSingleItemInAnyLocationIsReturned() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));

        Observable<List<FoodForListingBaseData>> actual = uut.getFood();

        testList(actual).assertValue(singletonList(FoodForListingBaseData.create(food.id(), food.name(), food.toBuy(), foodItem.eatBy())));
    }

    @Test
    public void foodWithTwoItemsInAnyLocationReturnsLeastEatBy() {
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .eatBy(Instant.EPOCH.plusSeconds(4))
                .build();
        FoodItemDbEntity foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .eatBy(Instant.EPOCH.plusSeconds(3))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(foodItem, foodItem2));

        Observable<List<FoodForListingBaseData>> actual = uut.getFood();

        testList(actual).assertValue(singletonList(FoodForListingBaseData.create(food.id(), food.name(), food.toBuy(), foodItem2.eatBy())));
    }
}