package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.RecipeHandler;
import de.njsm.stocks.server.v2.db.RecipeIngredientHandler;
import de.njsm.stocks.server.v2.db.RecipeProductHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeManagerTest {

    private RecipeManager uut;

    private RecipeHandler recipeHandler;

    private RecipeIngredientHandler ingredientHandler;

    private RecipeProductHandler productHandler;

    @Before
    public void setUp() throws Exception {
        recipeHandler = Mockito.mock(RecipeHandler.class);
        ingredientHandler = Mockito.mock(RecipeIngredientHandler.class);
        productHandler = Mockito.mock(RecipeProductHandler.class);
        uut = new RecipeManager(recipeHandler, ingredientHandler, productHandler);
        uut.setPrincipals(TEST_USER);
    }

    @After
    public void tearDown() throws Exception {
        verify(recipeHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(recipeHandler);
    }

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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
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

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
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
        RecipeForDeletion recipe = new RecipeForDeletion(1, 0);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(productHandler).deleteAllOf(recipe);
        verify(recipeHandler).delete(recipe);
        verify(recipeHandler).commit();
    }

    @Test
    public void failingIngredientDeletionPropagates() {
        RecipeForDeletion recipe = new RecipeForDeletion(1, 0);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(recipe);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(recipeHandler).rollback();
    }

    @Test
    public void failingProductDeletionPropagates() {
        RecipeForDeletion recipe = new RecipeForDeletion(1, 0);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(recipe);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).deleteAllOf(recipe);
        verify(productHandler).deleteAllOf(recipe);
        verify(recipeHandler).rollback();
    }

    @Test
    public void failingRecipeDeletionPropagates() {
        RecipeForDeletion recipe = new RecipeForDeletion(1, 0);
        when(ingredientHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(productHandler.deleteAllOf(recipe)).thenReturn(StatusCode.SUCCESS);
        when(recipeHandler.delete(recipe)).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.delete(recipe);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        verify(ingredientHandler).deleteAllOf(recipe);
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
