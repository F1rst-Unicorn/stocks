package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.FullRecipeForInsertion;
import de.njsm.stocks.server.v2.business.data.RecipeForInsertion;
import de.njsm.stocks.server.v2.business.data.RecipeIngredientForInsertion;
import de.njsm.stocks.server.v2.business.data.RecipeProductForInsertion;
import de.njsm.stocks.server.v2.db.RecipeHandler;
import de.njsm.stocks.server.v2.db.RecipeIngredientHandler;
import de.njsm.stocks.server.v2.db.RecipeProductHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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
        Mockito.verify(recipeHandler).setPrincipals(TEST_USER);
        Mockito.verifyNoMoreInteractions(recipeHandler);
    }

    @Test
    public void addingSimpleRecipeWorks() {
        RecipeForInsertion recipe = new RecipeForInsertion("name", "instructions", Duration.ZERO);
        FullRecipeForInsertion fullRecipeForInsertion = new FullRecipeForInsertion(recipe, Collections.emptyList(), Collections.emptyList());
        Mockito.when(recipeHandler.add(recipe)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(recipeHandler).add(fullRecipeForInsertion.getRecipe());
        Mockito.verify(recipeHandler).commit();
    }

    @Test
    public void addingRecipeWithIngredientWorks() {
        List<RecipeIngredientForInsertion> ingredients = List.of(new RecipeIngredientForInsertion(2, 3, 4, 5));
        RecipeForInsertion recipe = new RecipeForInsertion("name", "instructions", Duration.ZERO);
        FullRecipeForInsertion fullRecipeForInsertion = new FullRecipeForInsertion(recipe, ingredients, Collections.emptyList());
        Mockito.when(recipeHandler.add(recipe)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(recipeHandler).add(fullRecipeForInsertion.getRecipe());
        ArgumentCaptor<RecipeIngredientForInsertion> arguments = ArgumentCaptor.forClass(RecipeIngredientForInsertion.class);
        Mockito.verify(ingredientHandler, Mockito.times(ingredients.size())).add(arguments.capture());
        assertEquals(ingredients, arguments.getAllValues());
        Mockito.verify(recipeHandler).commit();
    }

    @Test
    public void addingRecipeWithOneFailingIngredientPropagates() {
        List<RecipeIngredientForInsertion> ingredients = List.of(new RecipeIngredientForInsertion(2, 3, 4, 5), new RecipeIngredientForInsertion(2, 3, 4, 5));
        RecipeForInsertion recipe = new RecipeForInsertion("name", "instructions", Duration.ZERO);
        FullRecipeForInsertion fullRecipeForInsertion = new FullRecipeForInsertion(recipe, ingredients, Collections.emptyList());
        Mockito.when(recipeHandler.add(recipe)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS, StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        ArgumentCaptor<RecipeIngredientForInsertion> arguments = ArgumentCaptor.forClass(RecipeIngredientForInsertion.class);
        Mockito.verify(ingredientHandler, Mockito.times(ingredients.size())).add(arguments.capture());
        assertEquals(ingredients, arguments.getAllValues());
        Mockito.verify(recipeHandler).rollback();
    }

    @Test
    public void addingRecipeWithIngredientAndProductsWorks() {
        List<RecipeIngredientForInsertion> ingredients = List.of(new RecipeIngredientForInsertion(2, 3, 4, 5));
        List<RecipeProductForInsertion> products = List.of(new RecipeProductForInsertion(2, 3, 4, 5));
        RecipeForInsertion recipe = new RecipeForInsertion("name", "instructions", Duration.ZERO);
        FullRecipeForInsertion fullRecipeForInsertion = new FullRecipeForInsertion(recipe, ingredients, products);
        Mockito.when(recipeHandler.add(recipe)).thenReturn(StatusCode.SUCCESS);
        Mockito.when(recipeHandler.commit()).thenReturn(StatusCode.SUCCESS);
        Mockito.when(ingredientHandler.add(any())).thenReturn(StatusCode.SUCCESS);
        Mockito.when(productHandler.add(any())).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.add(fullRecipeForInsertion);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(recipeHandler).add(fullRecipeForInsertion.getRecipe());
        ArgumentCaptor<RecipeIngredientForInsertion> ingredientArguments = ArgumentCaptor.forClass(RecipeIngredientForInsertion.class);
        Mockito.verify(ingredientHandler, Mockito.times(ingredients.size())).add(ingredientArguments.capture());
        assertEquals(ingredients, ingredientArguments.getAllValues());
        ArgumentCaptor<RecipeProductForInsertion> productArguments = ArgumentCaptor.forClass(RecipeProductForInsertion.class);
        Mockito.verify(productHandler, Mockito.times(products.size())).add(productArguments.capture());
        assertEquals(products, productArguments.getAllValues());
        Mockito.verify(recipeHandler).commit();
    }
}
