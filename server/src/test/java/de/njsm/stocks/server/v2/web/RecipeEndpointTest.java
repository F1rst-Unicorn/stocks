package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.RecipeManager;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FullRecipeForInsertion;
import de.njsm.stocks.server.v2.business.data.RecipeForInsertion;
import de.njsm.stocks.server.v2.web.data.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static org.junit.Assert.assertEquals;

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
        Mockito.when(recipeManager.add(input)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.put(createMockRequest(), input);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(recipeManager).add(input);
        Mockito.verify(recipeManager).setPrincipals(TEST_USER);
    }
}
