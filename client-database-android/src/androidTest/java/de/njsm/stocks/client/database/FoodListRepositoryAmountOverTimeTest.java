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

import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.PlotByUnit;
import de.njsm.stocks.client.business.entities.PlotPoint;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

public class FoodListRepositoryAmountOverTimeTest extends FoodListRepositoryImplTestBase {

    private FoodDbEntity food;

    private UnitDbEntity unit;

    private ScaledUnitDbEntity scaledUnit;

    private ScaledUnitDbEntity largerScaledUnit;

    private UnitDbEntity otherUnit;

    private ScaledUnitDbEntity otherScaledUnit;

    @Before
    public void setUp() {
        unit = standardEntities.unitDbEntity();
        otherUnit = standardEntities.unitDbEntityBuilder()
                .abbreviation("otherUnit")
                .build();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit, otherUnit));
        scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        largerScaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .scale(scaledUnit.scale().add(BigDecimal.TEN))
                .build();
        otherScaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(otherUnit.id())
                .scale(BigDecimal.valueOf(2))
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit, largerScaledUnit, otherScaledUnit));
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));
    }

    @Test
    public void noItemsGivesEmptyList() {
        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void singleItemIsReturned() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale())))
        ));
    }

    @Test
    public void removedItemDecreasesAmount() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        Instant deletionTime = foodItem.validTimeStart().plusSeconds(1);
        stocksDatabase.synchronisationDao().writeFoodItems(BitemporalOperations.currentDelete(foodItem, deletionTime));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale()),
                        PlotPoint.create(deletionTime, BigDecimal.ZERO)
                ))
        ));
    }

    @Test
    public void removedButUpdatedItemDecreasesAmount() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        Instant updateTime = foodItem.validTimeStart().plusSeconds(3);
        var update = BitemporalOperations.currentUpdate(foodItem, (BitemporalOperations.EntityEditor<FoodItemDbEntity, FoodItemDbEntity.Builder>) builder ->
                        builder.eatBy(foodItem.validTimeStart().plusSeconds(3)),
                updateTime);
        stocksDatabase.synchronisationDao().writeFoodItems(update);
        Instant deletionTime = foodItem.validTimeStart().plusSeconds(6);
        stocksDatabase.synchronisationDao().writeFoodItems(BitemporalOperations.currentDelete(update.get(2), deletionTime));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale()),
                        PlotPoint.create(deletionTime, BigDecimal.ZERO)
                ))
        ));
    }

    @Test
    public void amountsAreAccumulated() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        var secondFoodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .validTimeStart(foodItem.validTimeStart().plusSeconds(1))
                .transactionTimeStart(foodItem.transactionTimeStart().plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem, secondFoodItem));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale()),
                        PlotPoint.create(secondFoodItem.validTimeStart(), scaledUnit.scale().add(scaledUnit.scale()))
                ))
        ));
    }

    @Test
    public void differentlyScaledAmountsAreAccumulated() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        var secondFoodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(largerScaledUnit.id())
                .ofType(food.id())
                .validTimeStart(foodItem.validTimeStart().plusSeconds(1))
                .transactionTimeStart(foodItem.transactionTimeStart().plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem, secondFoodItem));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale()),
                        PlotPoint.create(secondFoodItem.validTimeStart(), scaledUnit.scale().add(largerScaledUnit.scale()))
                ))
        ));
    }

    @Test
    public void differentUnitsAreSeparated() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        var secondFoodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(otherScaledUnit.id())
                .ofType(food.id())
                .validTimeStart(foodItem.validTimeStart().plusSeconds(1))
                .transactionTimeStart(foodItem.transactionTimeStart().plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem, secondFoodItem));

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(Stream.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale())
                )),
                PlotByUnit.create(IdImpl.create(otherUnit.id()), otherUnit.abbreviation(), List.of(
                        PlotPoint.create(secondFoodItem.validTimeStart(), otherScaledUnit.scale())
                )))
                .sorted(comparing(PlotByUnit::id))
                .collect(Collectors.toList())
        );
    }

    @Test
    public void changingUnitResultsInADecreaseOfTheOldUnitAndAnIncreaseOfTheNew() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        Instant updateTime = foodItem.validTimeStart().plusSeconds(3);
        var update = BitemporalOperations.currentUpdate(foodItem, (BitemporalOperations.EntityEditor<FoodItemDbEntity, FoodItemDbEntity.Builder>) builder ->
                builder.unit(otherScaledUnit.id()),
                updateTime);
        stocksDatabase.synchronisationDao().writeFoodItems(update);

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(Stream.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale()),
                        PlotPoint.create(updateTime, BigDecimal.ZERO)
                )),
                PlotByUnit.create(IdImpl.create(otherUnit.id()), otherUnit.abbreviation(), List.of(
                        PlotPoint.create(updateTime, otherScaledUnit.scale())
                )))
                .sorted(comparing(PlotByUnit::id))
                .collect(Collectors.toList())
        );
    }

    @Test
    public void otherUpdatesAreInvisible() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit.id())
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        Instant updateTime = foodItem.validTimeStart().plusSeconds(3);
        var update = BitemporalOperations.currentUpdate(foodItem, (BitemporalOperations.EntityEditor<FoodItemDbEntity, FoodItemDbEntity.Builder>) builder ->
                builder.eatBy(foodItem.validTimeStart().plusSeconds(3)),
                updateTime);
        stocksDatabase.synchronisationDao().writeFoodItems(update);

        Observable<List<PlotByUnit<Instant>>> actual = uut.getAmountsOverTime(food::id);

        testList(actual).assertValue(List.of(
                PlotByUnit.create(IdImpl.create(unit.id()), unit.abbreviation(), List.of(
                        PlotPoint.create(foodItem.validTimeStart(), scaledUnit.scale())
                )))
        );
    }
}