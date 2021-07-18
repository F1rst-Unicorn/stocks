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
import de.njsm.stocks.server.v2.business.data.BitemporalRecipe;
import de.njsm.stocks.server.v2.business.data.Recipe;
import de.njsm.stocks.server.v2.business.data.RecipeForDeletion;
import de.njsm.stocks.server.v2.business.data.RecipeForInsertion;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class RecipeHandlerTest extends DbTestCase {

    private RecipeHandler uut;

    @Before
    public void setup() {
        uut = new RecipeHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void insertingWorks() {
        RecipeForInsertion data = new RecipeForInsertion("Soup", "Take water and carrots", Duration.ofMinutes(30));

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(2), result.success());
        Validation<StatusCode, Stream<Recipe>> recipes = uut.get(false, Instant.EPOCH);
        List<Recipe> list = recipes.success().collect(Collectors.toList());
        assertTrue(recipes.isSuccess());
        assertEquals(2, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Recipe>> result = uut.get(true, Instant.EPOCH);

        BitemporalRecipe sample = (BitemporalRecipe) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<Recipe>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalRecipe> data = result.success()
                .map(v -> (BitemporalRecipe) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Cake") &&
                        l.getInstructions().equals("Mix flour and sugar. Bake directly") &&
                        l.getDuration().equals(Duration.ofHours(1)) &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<Recipe>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<Recipe> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 1 &&
                        l.getVersion() == 0 &&
                        l.getName().equals("Cake") &&
                        l.getInstructions().equals("Mix flour and sugar. Bake directly") &&
                        l.getDuration().equals(Duration.ofHours(1))));
    }

    @Test
    public void deletingWorks() {
        StatusCode result = uut.delete(new RecipeForDeletion(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<Recipe>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }
}
