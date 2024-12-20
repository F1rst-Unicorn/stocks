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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import fj.data.Validation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeManagerGeneralTest extends RecipeManagerTestBase {

    @Test
    public void addingSimpleRecipeWorks() {
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(Collections.emptyList())
                .products(Collections.emptyList())
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(42));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertTrue(result.isSuccess());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        verify(recipeHandler).commit();
    }

    @Test
    public void addingRecipeWithIngredientWorks() {
        int recipeId = 42;
        List<RecipeIngredientForInsertion> ingredients = List.of(getTestIngredient());
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(ingredients)
                .products(Collections.emptyList())
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(recipeId));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertTrue(result.isSuccess());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        ArgumentCaptor<RecipeIngredientWithIdForInsertion> arguments = ArgumentCaptor.forClass(RecipeIngredientWithIdForInsertion.class);
        verify(ingredientHandler, Mockito.times(ingredients.size())).add(arguments.capture());
        assertEquals(ingredients.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), arguments.getAllValues());
        verify(recipeHandler).commit();
    }

    @Test
    public void addingRecipeWithTwoFailingIngredientPropagates() {
        int recipeId = 42;
        List<RecipeIngredientForInsertion> ingredients = List.of(getTestIngredient(), getTestIngredient());
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(ingredients)
                .products(Collections.emptyList())
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(recipeId));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS, StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        ArgumentCaptor<RecipeIngredientWithIdForInsertion> arguments = ArgumentCaptor.forClass(RecipeIngredientWithIdForInsertion.class);
        verify(ingredientHandler, Mockito.times(ingredients.size())).add(arguments.capture());
        assertEquals(ingredients.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), arguments.getAllValues());
        verify(recipeHandler).rollback();
    }

    @Test
    public void addingRecipeWithThreeFailingIngredientPropagates() {
        int recipeId = 42;
        List<RecipeIngredientForInsertion> ingredients = List.of(getTestIngredient(), getTestIngredient(), getTestIngredient());
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(ingredients)
                .products(Collections.emptyList())
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(recipeId));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS, StatusCode.SUCCESS, StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        ArgumentCaptor<RecipeIngredientWithIdForInsertion> arguments = ArgumentCaptor.forClass(RecipeIngredientWithIdForInsertion.class);
        verify(ingredientHandler, Mockito.times(ingredients.size())).add(arguments.capture());
        assertEquals(ingredients.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), arguments.getAllValues());
        verify(recipeHandler).rollback();
    }

    @Test
    public void addingRecipeWithIngredientAndProductsWorks() {
        int recipeId = 42;
        List<RecipeIngredientForInsertion> ingredients = List.of(getTestIngredient());
        List<RecipeProductForInsertion> products = List.of(getTestProduct());
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(ingredients)
                .products(products)
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(recipeId));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.add(any())).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertTrue(result.isSuccess());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        ArgumentCaptor<RecipeIngredientWithIdForInsertion> ingredientArguments = ArgumentCaptor.forClass(RecipeIngredientWithIdForInsertion.class);
        verify(ingredientHandler, Mockito.times(ingredients.size())).add(ingredientArguments.capture());
        assertEquals(ingredients.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), ingredientArguments.getAllValues());
        ArgumentCaptor<RecipeProductWithIdForInsertion> productArguments = ArgumentCaptor.forClass(RecipeProductWithIdForInsertion.class);
        verify(productHandler, Mockito.times(products.size())).add(productArguments.capture());
        assertEquals(products.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), productArguments.getAllValues());
        verify(recipeHandler).commit();
    }

    @Test
    public void addingRecipeWithFailingProductPropagates() {
        int recipeId = 42;
        List<RecipeIngredientForInsertion> ingredients = List.of(getTestIngredient());
        List<RecipeProductForInsertion> products = List.of(getTestProduct(), getTestProduct());
        RecipeForInsertion recipe = getTestRecipe();
        FullRecipeForInsertion fullRecipeForInsertion = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(ingredients)
                .products(products)
                .build();
        when(recipeHandler.addReturningId(recipe)).thenReturn(Validation.success(recipeId));
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.add(any())).thenReturn(StatusCode.SUCCESS, StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, Integer> result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        verify(recipeHandler).addReturningId(fullRecipeForInsertion.recipe());
        ArgumentCaptor<RecipeIngredientWithIdForInsertion> ingredientArguments = ArgumentCaptor.forClass(RecipeIngredientWithIdForInsertion.class);
        verify(ingredientHandler, Mockito.times(ingredients.size())).add(ingredientArguments.capture());
        assertEquals(ingredients.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), ingredientArguments.getAllValues());
        ArgumentCaptor<RecipeProductWithIdForInsertion> productArguments = ArgumentCaptor.forClass(RecipeProductWithIdForInsertion.class);
        verify(productHandler, Mockito.times(products.size())).add(productArguments.capture());
        assertEquals(products.stream().map(v -> v.withRecipe(recipeId)).collect(Collectors.toList()), productArguments.getAllValues());
        verify(recipeHandler).rollback();
    }

    @Test
    public void deletingARecipeWithIngredientsAndProductsCascades() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        verify(ingredientHandler).areEntitiesComplete(recipe, emptySet());
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(productHandler).areEntitiesComplete(recipe, emptySet());
        verify(productHandler).deleteAllOf(recipe);
        verify(recipeHandler).delete(recipe);
        verify(recipeHandler).commit();
    }

    @Test
    public void deletingARecipeWithInvalidIngredientsPropagates() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.INVALID_DATA_VERSION);
        when(recipeHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
        verify(ingredientHandler).areEntitiesComplete(recipe, emptySet());
        verify(recipeHandler).rollback();
    }

    @Test
    public void deletingARecipeWithInvalidProductsPropagates() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.INVALID_DATA_VERSION);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
        verify(ingredientHandler).areEntitiesComplete(recipe, emptySet());
        verify(productHandler).areEntitiesComplete(recipe, emptySet());
        verify(recipeHandler).rollback();
    }

    @Test
    public void failingIngredientDeletionPropagates() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(productHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(recipeHandler).rollback();
    }

    @Test
    public void failingProductDeletionPropagates() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).areEntitiesComplete(recipe, emptySet());
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(productHandler).areEntitiesComplete(recipe, emptySet());
        verify(productHandler).deleteAllOf(recipe);
        verify(recipeHandler).rollback();
    }

    @Test
    public void failingRecipeDeletionPropagates() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .products(emptySet())
                .build();
        when(ingredientHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.areEntitiesComplete(eq(recipe), any())).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).areEntitiesComplete(recipe, emptySet());
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(productHandler).areEntitiesComplete(recipe, emptySet());
        verify(productHandler).deleteAllOf(recipe);
        verify(recipeHandler).delete(recipe);
        verify(recipeHandler).rollback();
    }

    RecipeForInsertion getTestRecipe() {
        return RecipeForInsertion.builder()
                .name("name")
                .instructions("instructions")
                .duration(Duration.ZERO)
                .build();
    }

    RecipeIngredientForInsertion getTestIngredient() {
        return RecipeIngredientForInsertion.builder()
                .amount(2)
                .ingredient(3)
                .unit(5)
                .build();
    }

    RecipeProductForInsertion getTestProduct() {
        return RecipeProductForInsertion.builder()
                .amount(2)
                .product(3)
                .unit(5)
                .build();
    }
}
