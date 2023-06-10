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

import de.njsm.stocks.client.business.RecipeEditRepository;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.RecipeEditBaseData;
import de.njsm.stocks.client.business.entities.RecipeIngredientEditData;
import de.njsm.stocks.client.business.entities.RecipeProductEditData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.List.of;

public class RecipeEditRepositoryImplTest extends DbTestCase {

    private RecipeEditRepository uut;

    private RecipeDbEntity recipe;
    private RecipeIngredientDbEntity ingredient;
    private RecipeIngredientDbEntity ingredient2;
    private RecipeProductDbEntity product;
    private RecipeProductDbEntity product2;

    @Before
    public void setUp() {
        uut = new RecipeEditRepositoryImpl(
                stocksDatabase.recipeDao(),
                stocksDatabase.recipeIngredientDao(),
                stocksDatabase.recipeProductDao(),
                stocksDatabase.foodDao(),
                new ScaledUnitRepositoryImpl(stocksDatabase.scaledUnitDao()));

        recipe = standardEntities.recipeDbEntity();
        stocksDatabase.synchronisationDao().writeRecipes(of(recipe));
        ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        ingredient2 = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient, ingredient2));
        product = standardEntities.recipeProductDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        product2 = standardEntities.recipeProductDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeProducts(of(product, product2));
    }

    @Test
    public void gettingRecipeWorks() {

        var actual = uut.getRecipe(recipe::id);

        test(actual).assertValue(RecipeEditBaseData.create(recipe.id(), recipe.name(), recipe.instructions(), recipe.duration()));
    }

    @Test
    public void gettingRecipeIngredientsWorks() {
        var expected = new ArrayList<RecipeIngredientEditData>();
        expected.add(RecipeIngredientEditData.create(
                ingredient.id(),
                ingredient.amount(),
                IdImpl.create(ingredient.unit()),
                IdImpl.create(ingredient.ingredient())
        ));
        expected.add(RecipeIngredientEditData.create(
                ingredient2.id(),
                ingredient2.amount(),
                IdImpl.create(ingredient2.unit()),
                IdImpl.create(ingredient2.ingredient())
        ));
        expected.sort(Comparator.comparing(RecipeIngredientEditData::id));

        var actual = uut.getIngredients(recipe::id);

        testList(actual).assertValue(expected);
    }

    @Test
    public void gettingRecipeProductsWorks() {
        var expected = new ArrayList<RecipeProductEditData>();
        expected.add(RecipeProductEditData.create(
                product.id(),
                product.amount(),
                IdImpl.create(product.unit()),
                IdImpl.create(product.product())
        ));
        expected.add(RecipeProductEditData.create(
                product2.id(),
                product2.amount(),
                IdImpl.create(product2.unit()),
                IdImpl.create(product2.product())
        ));
        expected.sort(Comparator.comparing(RecipeProductEditData::id));

        var actual = uut.getProducts(recipe::id);

        testList(actual).assertValue(expected);
    }
}