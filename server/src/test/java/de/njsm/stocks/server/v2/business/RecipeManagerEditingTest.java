/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeManagerEditingTest extends RecipeManagerTestBase {

    private static class TestData {

        final RecipeForEditing recipe = RecipeForEditing.builder()
                .id(1)
                .version(0)
                .name("name")
                .instructions("instructions")
                .duration(Duration.ofHours(1))
                .build();

        final RecipeIngredientForEditing ingredientForEditing = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .recipe(3)
                .ingredient(4)
                .unit(5)
                .build();

        final RecipeIngredientForDeletion ingredientForDeletion = RecipeIngredientForDeletion.builder()
                .id(1)
                .version(2)
                .build();

        final RecipeIngredientForInsertion ingredientForInsertion = RecipeIngredientForInsertion.builder()
                .amount(1)
                .ingredient(2)
                .unit(3)
                .build();

        final RecipeProductForEditing productForEditing = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .recipe(3)
                .product(4)
                .unit(5)
                .build();

        final RecipeProductForDeletion productForDeletion = RecipeProductForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        final RecipeProductForInsertion productForInsertion = RecipeProductForInsertion.builder()
                .amount(1)
                .product(2)
                .unit(3)
                .build();

        final FullRecipeForEditing.Builder fullRecipe = FullRecipeForEditing.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .ingredientsToInsert(emptySet())
                .ingredientsToDelete(emptySet())
                .products(emptySet())
                .productsToInsert(emptySet())
                .productsToDelete(emptySet());
    }

    private final TestData testData = new TestData();

    @Test
    void editingWithWrongIngredientsPropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .ingredientsToDelete(Set.of(testData.ingredientForDeletion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients()))
                .thenReturn(StatusCode.INVALID_DATA_VERSION);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.INVALID_DATA_VERSION));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithWrongProductsPropagates() {
        RecipeProductForDeletion productForDeletion = RecipeProductForDeletion.builder()
                .id(1)
                .version(2)
                .build();
        FullRecipeForEditing input = testData.fullRecipe
                .productsToDelete(Set.of(productForDeletion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.INVALID_DATA_VERSION);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.INVALID_DATA_VERSION));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingIngredientsPropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .ingredients(Set.of(testData.ingredientForEditing))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.edit(testData.ingredientForEditing)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(ingredientHandler).edit(testData.ingredientForEditing);
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingIngredientsToDeletePropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .ingredientsToDelete(Set.of(testData.ingredientForDeletion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.delete(testData.ingredientForDeletion)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(ingredientHandler).delete(testData.ingredientForDeletion);
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingIngredientsToInsertPropagates() {
        int recipeId = testData.recipe.id();
        FullRecipeForEditing input = testData.fullRecipe
                .ingredientsToInsert(Set.of(testData.ingredientForInsertion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(testData.ingredientForInsertion.withRecipe(recipeId))).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(ingredientHandler).add(testData.ingredientForInsertion.withRecipe(recipeId));
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingProductPropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .products(Set.of(testData.productForEditing))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.edit(testData.productForEditing)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(productHandler).edit(testData.productForEditing);
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingProductToDeletePropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .productsToDelete(Set.of(testData.productForDeletion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.delete(testData.productForDeletion)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(productHandler).delete(testData.productForDeletion);
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingProductToInsertPropagates() {
        int recipeId = testData.recipe.id();
        FullRecipeForEditing input = testData.fullRecipe
                .productsToInsert(Set.of(testData.productForInsertion))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.add(testData.productForInsertion.withRecipe(recipeId))).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(productHandler).add(testData.productForInsertion.withRecipe(recipeId));
        verify(recipeHandler).rollback();
    }

    @Test
    void editingWithFailingRecipePropagates() {
        FullRecipeForEditing input = testData.fullRecipe
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.edit(testData.recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.DATABASE_UNREACHABLE));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(recipeHandler).edit(testData.recipe);
        verify(recipeHandler).rollback();
    }

    @Test
    void editingSuccessfullyWorks() {
        FullRecipeForEditing input = testData.fullRecipe
                .ingredients(Set.of(testData.ingredientForEditing))
                .products(Set.of(testData.productForEditing))
                .build();
        when(ingredientHandler.areEntitiesComplete(testData.recipe, input.existingIngredients())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(testData.recipe, input.existingProducts())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.edit(testData.ingredientForEditing)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.edit(testData.productForEditing)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.edit(testData.recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.edit(input);

        assertThat(result, is(StatusCode.SUCCESS));
        verify(ingredientHandler).areEntitiesComplete(testData.recipe, input.existingIngredients());
        verify(productHandler).areEntitiesComplete(testData.recipe, input.existingProducts());
        verify(ingredientHandler).edit(testData.ingredientForEditing);
        verify(productHandler).edit(testData.productForEditing);
        verify(recipeHandler).edit(testData.recipe);
        verify(recipeHandler).commit();
    }
}
