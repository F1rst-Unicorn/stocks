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

import de.njsm.stocks.client.business.RecipeDeleteRepository;
import org.junit.Before;
import org.junit.Test;

import static java.util.List.of;
import static junit.framework.TestCase.assertEquals;

public class RecipeDeleteRepositoryImplTest extends DbTestCase {

    private RecipeDeleteRepository uut;

    @Before
    public void setUp() {
        uut = new RecipeDeleteRepositoryImpl(stocksDatabase.recipeDao(), stocksDatabase.recipeIngredientDao(), stocksDatabase.recipeProductDao());
    }

    @Test
    public void gettingRecipeWorks() {
        var recipe = standardEntities.recipeDbEntity();
        stocksDatabase.synchronisationDao().writeRecipes(of(recipe));
        var ingredient = standardEntities.recipeIngredientDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeIngredients(of(ingredient));
        var product = standardEntities.recipeProductDbEntityBuilder()
                .recipe(recipe.id())
                .build();
        stocksDatabase.synchronisationDao().writeRecipeProducts(of(product));

        var actual = uut.getData(recipe::id);

        assertEquals(recipe.id(), actual.recipe().id());
        assertEquals(recipe.version(), actual.recipe().version());
        assertEquals(1, actual.ingredients().size());
        assertEquals(ingredient.id(), actual.ingredients().get(0).id());
        assertEquals(ingredient.version(), actual.ingredients().get(0).version());
        assertEquals(1, actual.products().size());
        assertEquals(product.id(), actual.products().get(0).id());
        assertEquals(product.version(), actual.products().get(0).version());
    }
}