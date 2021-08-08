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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesVersionable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class RecipeHandlerTest extends DbTestCase implements EntityDbTestCase<RecipeRecord, Recipe> {

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
        RecipeForInsertion data = RecipeForInsertion.builder()
                .name("Soup")
                .instructions("Take water and carrots")
                .duration(Duration.ofMinutes(30))
                .build();

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertInsertableIsInserted(result, data, 2, 2);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Recipe>> result = uut.get(true, Instant.EPOCH);

        BitemporalRecipe sample = (BitemporalRecipe) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<Recipe>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalRecipe> data = result.success()
                .map(v -> (BitemporalRecipe) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Cake") &&
                        l.instructions().equals("Mix flour and sugar. Bake directly") &&
                        l.duration().equals(Duration.ofHours(1)) &&
                        l.initiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<Recipe>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<Recipe> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Cake") &&
                        l.instructions().equals("Mix flour and sugar. Bake directly") &&
                        l.duration().equals(Duration.ofHours(1))));
    }

    @Test
    public void editingWorks() {
        RecipeForEditing recipe = RecipeForEditing.builder()
                .id(1)
                .version(0)
                .name("Bread")
                .instructions("Add bread")
                .duration(Duration.ofHours(2))
                .build();

        StatusCode result = uut.edit(recipe);

        assertThat(result, is(StatusCode.SUCCESS));
        Validation<StatusCode, Stream<Recipe>> recipes = uut.get(false, Instant.EPOCH);
        assertTrue(recipes.isSuccess());
        List<Recipe> list = recipes.success().collect(Collectors.toList());
        assertEquals(1, list.size());
        assertThat(list, hasItem(matchesVersionable(recipe)));
    }

    @Test
    public void editingWithoutChangeIsRejected() {
        RecipeForEditing recipe = RecipeForEditing.builder()
                .id(1)
                .version(0)
                .name("Cake")
                .instructions("Mix flour and sugar. Bake directly")
                .duration(Duration.ofHours(1))
                .build();

        StatusCode result = uut.edit(recipe);

        assertThat(result, is(StatusCode.INVALID_DATA_VERSION));
    }

    @Test
    public void deletingWorks() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.delete(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<Recipe>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }

    @Override
    public CrudDatabaseHandler<RecipeRecord, Recipe> getDbHandler() {
        return uut;
    }
}
