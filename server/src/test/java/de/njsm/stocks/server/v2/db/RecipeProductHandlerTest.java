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
import de.njsm.stocks.server.v2.business.data.BitemporalRecipeProduct;
import de.njsm.stocks.server.v2.business.data.RecipeProduct;
import de.njsm.stocks.server.v2.business.data.RecipeProductForDeletion;
import de.njsm.stocks.server.v2.business.data.RecipeProductForInsertion;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class RecipeProductHandlerTest extends DbTestCase {

    private RecipeProductHandler uut;

    @Before
    public void setup() {
        uut = new RecipeProductHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void insertingWorks() {
        RecipeProductForInsertion data = new RecipeProductForInsertion(5, 3, 1, 1);

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(2), result.success());
        Validation<StatusCode, Stream<RecipeProduct>> recipeIngredients = uut.get(false, Instant.EPOCH);
        List<RecipeProduct> list = recipeIngredients.success().collect(Collectors.toList());
        assertTrue(recipeIngredients.isSuccess());
        assertEquals(2, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<RecipeProduct>> result = uut.get(true, Instant.EPOCH);

        BitemporalRecipeProduct sample = (BitemporalRecipeProduct) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<RecipeProduct>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalRecipeProduct> data = result.success()
                .map(v -> (BitemporalRecipeProduct) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getAmount() == 2 &&
                        l.getProduct() == 3 &&
                        l.getRecipe() == 1 &&
                        l.getUnit() == 2 &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<RecipeProduct>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<RecipeProduct> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getAmount() == 2 &&
                        l.getProduct() == 3 &&
                        l.getRecipe() == 1 &&
                        l.getUnit() == 2));
    }

    @Test
    public void deletingWorks() {
        StatusCode result = uut.delete(new RecipeProductForDeletion(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<RecipeProduct>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }
}
