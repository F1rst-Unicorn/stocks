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

import de.njsm.stocks.client.business.entities.RecipeForListingBaseData;
import de.njsm.stocks.client.business.entities.RecipeIngredientAmount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;

public class RecipeListRepositoryImplTest extends DbTestCase {

    private RecipeListRepositoryImpl uut;
    private UnitDbEntity unit1;
    private UnitDbEntity unit2;
    private UnitDbEntity unit3;
    private ScaledUnitDbEntity scaledUnit1;
    private ScaledUnitDbEntity scaledUnit2;
    private ScaledUnitDbEntity scaledUnit3;

    @Before
    public void setUp() {
        uut = new RecipeListRepositoryImpl(stocksDatabase.recipeDao());

        unit1 = standardEntities.unitDbEntity();
        unit2 = standardEntities.unitDbEntity();
        unit3 = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(of(unit1, unit2, unit3));
        scaledUnit1 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit1.id())
                .scale(BigDecimal.valueOf(2))
                .build();
        scaledUnit2 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit2.id())
                .scale(BigDecimal.valueOf(4))
                .build();
        scaledUnit3 = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit3.id())
                .scale(BigDecimal.valueOf(5))
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(of(scaledUnit1, scaledUnit2, scaledUnit3));
    }

    @Test
    public void gettingRecipeWorks() {
        var recipe = standardEntities.recipeDbEntity();
        stocksDatabase.synchronisationDao().writeRecipes(of(recipe));

        var actual = uut.get();

        testList(actual).assertValue(of(RecipeForListingBaseData.create(
                recipe.id(),
                recipe.name()
        )));
    }

    @Test
    public void gettingSingleIngredientWithoutPresentDataWorks() {
        var ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient));

        var actual = uut.getIngredients();

        testList(actual).assertValue(of(
                RecipeIngredientAmount.create(ingredient.recipe(),
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient.amount()),
                        of())
        ));
    }

    @Test
    public void gettingSingleIngredientWithSingleDataWorks() {
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem));
        var ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem.ofType())
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient));

        var actual = uut.getIngredients();

        testList(actual).assertValue(of(
                RecipeIngredientAmount.create(ingredient.recipe(),
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient.amount()),
                        of(
                                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 1)
                        ))
        ));
    }

    @Test
    public void gettingSingleIngredientsWithTwoFoodItemsWorks() {
        var foodItem1 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodItem1.ofType())
                .unit(scaledUnit2.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem1, foodItem2));
        var ingredient1 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem1.ofType())
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient1));

        var actual = uut.getIngredients();

        testList(actual).assertValue(of(
                RecipeIngredientAmount.create(ingredient1.recipe(),
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient1.amount()),
                        fixedOrder(Comparator.comparingInt(RecipeIngredientAmount.Amount::unit),
                                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 1),
                                RecipeIngredientAmount.Amount.create(unit2.id(), scaledUnit2.scale(), 1)
                        ))
        ));
    }

    @Test
    public void gettingSingleIngredientWithTwoGroupedFoodItemsWorks() {
        var foodItem1 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodItem1.ofType())
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem1, foodItem2));
        var ingredient1 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem1.ofType())
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient1));

        var actual = uut.getIngredients();

        testList(actual).assertValue(of(
                RecipeIngredientAmount.create(ingredient1.recipe(),
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient1.amount()),
                        of(
                                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 2)
                        ))
        ));
    }

    @Test
    public void gettingTwoIngredientsWithTwoGroupedFoodItemsWorks() {
        var foodItem1 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        var foodItem2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(foodItem1.ofType())
                .unit(scaledUnit2.id())
                .build();
        var foodItem3 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit3.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem1, foodItem2, foodItem3));
        var ingredient1 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem1.ofType())
                .unit(scaledUnit1.id())
                .build();
        var ingredient2 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem3.ofType())
                .unit(scaledUnit3.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient1, ingredient2));

        var actual = uut.getIngredients();

        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient1.recipe(),
                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient1.amount()),
                fixedOrder(Comparator.comparingInt(RecipeIngredientAmount.Amount::unit),
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 1),
                        RecipeIngredientAmount.Amount.create(unit2.id(), scaledUnit2.scale(), 1)
                ))));
        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient2.recipe(),
                RecipeIngredientAmount.Amount.create(unit3.id(), scaledUnit3.scale(), ingredient2.amount()),
                of(
                        RecipeIngredientAmount.Amount.create(unit3.id(), scaledUnit3.scale(), 1)
                ))));
    }

    @Test
    public void gettingFourIngredientsWithEmptyMiddleOneWorks() {
        var foodItem1 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        var foodItem3 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit3.id())
                .build();
        var foodItem4 = standardEntities.foodItemDbEntityBuilder()
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(foodItem1, foodItem3, foodItem4));
        var ingredient1 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem1.ofType())
                .unit(scaledUnit1.id())
                .build();
        var ingredient2 = standardEntities.recipeIngredientDbEntityBuilder()
                .unit(scaledUnit2.id())
                .build();
        var ingredient3 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem3.ofType())
                .unit(scaledUnit3.id())
                .build();
        var ingredient4 = standardEntities.recipeIngredientDbEntityBuilder()
                .ingredient(foodItem4.ofType())
                .unit(scaledUnit1.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient1, ingredient2, ingredient3, ingredient4));

        var actual = uut.getIngredients();

        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient1.recipe(),
                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient1.amount()),
                of(
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 1)
                ))));
        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient2.recipe(),
                RecipeIngredientAmount.Amount.create(unit2.id(), scaledUnit2.scale(), ingredient2.amount()),
                emptyList())));
        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient3.recipe(),
                RecipeIngredientAmount.Amount.create(unit3.id(), scaledUnit3.scale(), ingredient3.amount()),
                of(
                        RecipeIngredientAmount.Amount.create(unit3.id(), scaledUnit3.scale(), 1)
                ))));
        testList(actual).assertValue(v -> v.contains(RecipeIngredientAmount.create(ingredient4.recipe(),
                RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), ingredient4.amount()),
                of(
                        RecipeIngredientAmount.Amount.create(unit1.id(), scaledUnit1.scale(), 1)
                ))));
    }

    @SafeVarargs
    static <E> List<E> fixedOrder(Comparator<E> comparator, E... elements) {
        return Stream.of(elements)
                .sorted(comparator)
                .collect(toList());
    }
}