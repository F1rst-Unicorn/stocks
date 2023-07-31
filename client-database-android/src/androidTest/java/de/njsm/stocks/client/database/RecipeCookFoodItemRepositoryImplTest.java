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

import de.njsm.stocks.client.business.RecipeCookRepository;
import de.njsm.stocks.client.business.entities.FoodItemForDeletion;
import de.njsm.stocks.client.business.entities.RecipeCookingIngredientToConsume;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.List.of;
import static org.junit.Assert.assertEquals;

public class RecipeCookFoodItemRepositoryImplTest extends DbTestCase {

    private RecipeCookRepository uut;

    private FoodDbEntity food;
    private FoodDbEntity food2;
    private ScaledUnitDbEntity scaledUnit;
    private ScaledUnitDbEntity scaledUnit2;


    @Before
    public void setUp() {
        uut = new RecipeCookRepositoryImpl(
                stocksDatabase.recipeDao(),
                stocksDatabase.recipeIngredientDao(),
                stocksDatabase.recipeProductDao(),
                stocksDatabase.foodItemDao());

        scaledUnit = standardEntities.scaledUnitDbEntityBuilder().build();
        scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder().build();
        stocksDatabase.synchronisationDao().writeScaledUnits(of(scaledUnit, scaledUnit2));
        food = standardEntities.foodDbEntity();
        food2 = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(of(food, food2));

    }

    @Test
    public void noIngredientsYieldsEmptyResult() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        var actual = uut.getFoodItemsForCooking(List.of());

        assertEquals(List.of(), actual);
    }

    @Test
    public void singleFoodItemIsSelected() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        var ingredient = RecipeCookingIngredientToConsume.create(
                create(food.id()),
                create(scaledUnit.id()),
                1
        );

        var actual = uut.getFoodItemsForCooking(List.of(ingredient));

        assertEquals(List.of(FoodItemForDeletion.create(foodItem.id(), foodItem.version())),
                actual);
    }

    @Test
    public void foodItemOfDifferentFoodIsIgnored() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        var ingredient = RecipeCookingIngredientToConsume.create(
                create(food2.id()),
                create(scaledUnit.id()),
                1
        );

        var actual = uut.getFoodItemsForCooking(List.of(ingredient));

        assertEquals(List.of(), actual);
    }

    @Test
    public void foodItemOfDifferentScaledUnitIsIgnored() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));
        var ingredient = RecipeCookingIngredientToConsume.create(
                create(food.id()),
                create(scaledUnit2.id()),
                1
        );

        var actual = uut.getFoodItemsForCooking(List.of(ingredient));

        assertEquals(List.of(), actual);
    }

    @Test
    public void foodItemsAreOnlyReturnedUpToTheLimit() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem, foodItem2));
        var ingredient = RecipeCookingIngredientToConsume.create(
                create(food.id()),
                create(scaledUnit.id()),
                1
        );

        var actual = uut.getFoodItemsForCooking(List.of(ingredient));

        assertEquals(ingredient.count(), actual.size());
    }

    @Test
    public void earliestExpiringItemIsReturned() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .eatBy(Instant.EPOCH)
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .eatBy(Instant.EPOCH.plusSeconds(1))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem, foodItem2));
        var ingredient = RecipeCookingIngredientToConsume.create(
                create(food.id()),
                create(scaledUnit.id()),
                1
        );

        var actual = uut.getFoodItemsForCooking(List.of(ingredient));

        assertEquals(List.of(FoodItemForDeletion.create(foodItem.id(), foodItem.version())),
                actual);
    }
}