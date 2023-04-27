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

import de.njsm.stocks.client.business.entities.PlotPoint;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.emptyList;

public class FoodListRepositoryEatByExpirationHistogramTest extends FoodListRepositoryImplTestBase {

    private FoodDbEntity food;

    @Before
    public void setUp() {
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));
    }

    @Test
    public void noItemsGivesEmptyList() {
        Observable<List<PlotPoint<Integer>>> actual = uut.getEatByExpirationHistogram(food::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void singlePresentItemIsNotCounted() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        Observable<List<PlotPoint<Integer>>> actual = uut.getEatByExpirationHistogram(food::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void singleEatenItemIsCounted() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .validTimeEnd(Instant.EPOCH)
                .eatBy(Instant.EPOCH)
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        Observable<List<PlotPoint<Integer>>> actual = uut.getEatByExpirationHistogram(food::id);

        testList(actual).assertValue(List.of(
                PlotPoint.create(0, BigDecimal.ONE)
        ));
    }

    @Test
    public void singleEatenItemOnSameDayIsCounted() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .validTimeEnd(Instant.EPOCH.plus(Duration.ofHours(14)))
                .eatBy(Instant.EPOCH)
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        Observable<List<PlotPoint<Integer>>> actual = uut.getEatByExpirationHistogram(food::id);

        testList(actual).assertValue(List.of(
                PlotPoint.create(0, BigDecimal.ONE)
        ));
    }

    @Test
    public void twoEatenItemsOnSameDayAreCounted() {
        var foodItem1 = standardEntities.foodItemDbEntityBuilder()
                .validTimeEnd(Instant.EPOCH.plus(Duration.ofHours(14)))
                .eatBy(Instant.EPOCH)
                .ofType(food.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .validTimeEnd(Instant.EPOCH.plus(Duration.ofHours(15)))
                .eatBy(Instant.EPOCH)
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem1, foodItem2));

        Observable<List<PlotPoint<Integer>>> actual = uut.getEatByExpirationHistogram(food::id);

        testList(actual).assertValue(List.of(
                PlotPoint.create(0, BigDecimal.valueOf(2))
        ));
    }
}