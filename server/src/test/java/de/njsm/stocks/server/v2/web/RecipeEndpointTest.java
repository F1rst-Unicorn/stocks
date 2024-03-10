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

package de.njsm.stocks.server.v2.web;

import com.google.common.collect.ImmutableSet;
import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.RecipeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static de.njsm.stocks.server.v2.web.Util.createMockRequest;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeEndpointTest {

    private RecipeEndpoint uut;

    private RecipeManager recipeManager;

    @BeforeEach
    public void setUp() throws Exception {
        recipeManager = Mockito.mock(RecipeManager.class);
        uut = new RecipeEndpoint(recipeManager);
    }

    @AfterEach
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
    void validEditingIsForwarded() {
        RecipeForEditing recipe = RecipeForEditing.builder()
                .id(1)
                .version(0)
                .name("name")
                .instructions("instructions")
                .duration(Duration.ofHours(1))
                .build();
        FullRecipeForEditing input = FullRecipeForEditing.builder()
                .recipe(recipe)
                .ingredients(emptySet())
                .ingredientsToInsert(emptySet())
                .ingredientsToDelete(emptySet())
                .products(emptySet())
                .productsToInsert(emptySet())
                .productsToDelete(emptySet())
                .build();
        when(recipeManager.edit(input)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.edit(createMockRequest(), input);

        assertThat(result.getStatus(), is(StatusCode.SUCCESS));
        verify(recipeManager).setPrincipals(TEST_USER);
        verify(recipeManager).edit(input);
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
