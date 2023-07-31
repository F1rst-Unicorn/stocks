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
import de.njsm.stocks.client.business.entities.FoodItemForCooking;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataProduct;
import de.njsm.stocks.client.business.entities.RecipeForCooking;
import de.njsm.stocks.client.business.entities.RecipeIngredientForCooking;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.emptyList;
import static java.util.List.of;

public class RecipeCookRepositoryImplTest extends DbTestCase {

    private RecipeCookRepository uut;

    private RecipeDbEntity recipe;
    private RecipeIngredientDbEntity ingredient;
    private RecipeIngredientDbEntity ingredient2;
    private RecipeProductDbEntity product;
    private RecipeProductDbEntity product2;
    private FoodDbEntity food;
    private FoodDbEntity food2;
    private ScaledUnitDbEntity scaledUnit;
    private ScaledUnitDbEntity scaledUnit2;
    private UnitDbEntity unit;
    private UnitDbEntity unit2;

    @Before
    public void setUp() {
        uut = new RecipeCookRepositoryImpl(
                stocksDatabase.recipeDao(),
                stocksDatabase.recipeIngredientDao(),
                stocksDatabase.recipeProductDao(),
                stocksDatabase.foodItemDao());

        unit = standardEntities.unitDbEntity();
        unit2 = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(of(unit, unit2));
        scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(of(scaledUnit, scaledUnit2));
        food = standardEntities.foodDbEntity();
        food2 = standardEntities.foodDbEntityBuilder()
                .name(food.name() + "2")
                .build();
        stocksDatabase.synchronisationDao().writeFood(of(food, food2));
        recipe = standardEntities.recipeDbEntity();
        stocksDatabase.synchronisationDao().writeRecipes(of(recipe));
        ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .ingredient(food.id())
                .unit(scaledUnit.id())
                .build();
        ingredient2 = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .ingredient(food2.id())
                .unit(scaledUnit2.id())
                .amount(ingredient.amount() + 1)
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient, ingredient2));
        product = standardEntities.recipeProductDbEntityBuilder()
                .recipe(recipe.id())
                .product(food.id())
                .unit(scaledUnit.id())
                .build();
        product2 = standardEntities.recipeProductDbEntityBuilder()
                .recipe(recipe.id())
                .product(food2.id())
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeProducts(of(product, product2));
    }

    @Test
    public void gettingRecipeWorks() {

        var actual = uut.getRecipe(create(recipe.id()));

        test(actual).assertValue(RecipeForCooking.create(create(recipe.id()), recipe.name()));
    }

    @Test
    public void gettingProductsWorks() {

        var actual = uut.getProducts(create(recipe.id()));

        testList(actual).assertValue(List.of(RecipeCookingFormDataProduct.create(
                create(food.id()),
                food.name(),
                RecipeCookingFormDataProduct.Amount.create(create(scaledUnit.id()), scaledUnit.scale(), unit.abbreviation(), product.amount())
        ), RecipeCookingFormDataProduct.create(
                create(food2.id()),
                food2.name(),
                RecipeCookingFormDataProduct.Amount.create(create(scaledUnit2.id()), scaledUnit2.scale(), unit2.abbreviation(), product2.amount())
        )));
    }

    @Test
    public void gettingRequiredIngredientsWorks() {

        var actual = uut.getRequiredIngredients(create(recipe.id()));

        testList(actual).assertValue(List.of(RecipeIngredientForCooking.create(
                        create(food.id()),
                        food.name(),
                        food.toBuy(),
                        create(unit.id()),
                        unit.abbreviation(),
                        scaledUnit.scale().multiply(new BigDecimal(ingredient.amount()))),
                RecipeIngredientForCooking.create(
                        create(food2.id()),
                        food2.name(),
                        food2.toBuy(),
                        create(unit2.id()),
                        unit2.abbreviation(),
                        scaledUnit2.scale().multiply(new BigDecimal(ingredient2.amount())))
        ));
    }

    @Test
    public void gettingPresentIngredientsWithoutFoodItemsGivesEmptyList() {

        var actual = uut.getPresentIngredients(create(recipe.id()));

        test(actual).assertValue(emptyList());
    }

    @Test
    public void gettingPresentIngredientsWithTwoFoodItemsGivesAggregatedResult() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem, foodItem2));

        var actual = uut.getPresentIngredients(create(recipe.id()));

        testList(actual).assertValue(List.of(
                FoodItemForCooking.create(
                        create(food.id()),
                        create(unit.id()),
                        unit.abbreviation(),
                        create(scaledUnit.id()),
                        scaledUnit.scale(),
                        2
                )
        ));
    }

    @Test
    public void gettingPresentIngredientsWithTwoIngredientsOfSameFoodMergesItems() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem, foodItem2));
        ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .ingredient(food.id())
                .unit(scaledUnit.id())
                .build();
        ingredient2 = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .ingredient(food.id())
                .unit(scaledUnit2.id())
                .amount(ingredient.amount() + 1)
                .build();
        stocksDatabase.synchronisationDao().synchroniseRecipeIngredients(of(ingredient, ingredient2));

        var actual = uut.getPresentIngredients(create(recipe.id()));

        testList(actual).assertValue(List.of(
                FoodItemForCooking.create(
                        create(food.id()),
                        create(unit.id()),
                        unit.abbreviation(),
                        create(scaledUnit.id()),
                        scaledUnit2.scale(),
                        2
                )
        ));
    }
}