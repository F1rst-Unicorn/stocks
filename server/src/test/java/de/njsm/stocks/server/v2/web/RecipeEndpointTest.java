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
    public void invalidRequestIsRejected() {
        FullRecipeForInsertion input = new FullRecipeForInsertion(null, null, null);

        Response result = uut.put(createMockRequest(), input);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void validRequestIsForwarded() {
        FullRecipeForInsertion input = new FullRecipeForInsertion(new RecipeForInsertion("", "", Duration.ZERO), Collections.emptyList(), Collections.emptyList());
        Mockito.when(recipeManager.add(input)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.put(createMockRequest(), input);

        assertEquals(StatusCode.SUCCESS, result.getStatus());
        Mockito.verify(recipeManager).add(input);
    }
}
