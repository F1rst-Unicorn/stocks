package de.njsm.stocks.server.v2.web;

import com.google.common.collect.ImmutableSet;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.FullRecipeForDeletion;
import de.njsm.stocks.common.api.impl.FullRecipeForInsertion;
import de.njsm.stocks.common.api.impl.RecipeForDeletion;
import de.njsm.stocks.common.api.impl.RecipeForInsertion;
import de.njsm.stocks.server.v2.business.RecipeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeEndpointTest {

    private RecipeEndpoint uut;

    private RecipeManager recipeManager;

    @Before
    public void setUp() throws Exception {
        recipeManager = Mockito.mock(RecipeManager.class);
        uut = new RecipeEndpoint(recipeManager);
    }

    @After
    public void tearDown() throws Exception {
        Mockito.verifyNoMoreInteractions(recipeManager);
    }

    @Test
    public void validRequestIsForwarded() {
        RecipeForInsertion recipe = RecipeForInsertion.builder()
                .name("")
                .instructions("")
                .duration(Duration.ZERO)
                .build();
        FullRecipeForInsertion input = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(Collections.emptyList())
                .products(Collections.emptyList())
                .build();
        when(recipeManager.add(input)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.put(createMockRequest(), input);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        verify(recipeManager).add(input);
        verify(recipeManager).setPrincipals(TEST_USER);
    }

    @Test
    public void deletingRecipeIsForwarded() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        FullRecipeForDeletion input = FullRecipeForDeletion.builder()
                .recipe(recipe)
                .ingredients(ImmutableSet.of())
                .products(ImmutableSet.of())
                .build();
        when(recipeManager.delete(input)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.delete(createMockRequest(), input);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        verify(recipeManager).setPrincipals(TEST_USER);
        verify(recipeManager).delete(input);
    }
}
