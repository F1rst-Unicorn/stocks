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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.BitemporalRecipeIngredient;
import de.njsm.stocks.server.v2.business.data.RecipeIngredient;
import de.njsm.stocks.server.v2.business.data.RecipeIngredientForDeletion;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.Assert.*;

public class RecipeIngredientHandlerTest extends DbTestCase {

    private RecipeIngredientHandler uut;

    @Before
    public void setup() {
        uut = new RecipeIngredientHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(true, Instant.EPOCH);

        BitemporalRecipeIngredient sample = (BitemporalRecipeIngredient) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalRecipeIngredient> data = result.success()
                .map(v -> (BitemporalRecipeIngredient) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getAmount() == 2 &&
                        l.getIngredient() == 3 &&
                        l.getRecipe() == 1 &&
                        l.getUnit() == 2 &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<RecipeIngredient> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getAmount() == 2 &&
                        l.getIngredient() == 3 &&
                        l.getRecipe() == 1 &&
                        l.getUnit() == 2));
    }

    @Test
    public void deletingWorks() {
        StatusCode result = uut.delete(new RecipeIngredientForDeletion(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<RecipeIngredient>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }
}
