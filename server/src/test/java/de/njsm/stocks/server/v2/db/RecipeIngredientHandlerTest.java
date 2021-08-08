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
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeIngredientHandlerTest extends DbTestCase implements EntityDbTestCase<RecipeIngredientRecord, RecipeIngredient> {

    private RecipeIngredientHandler uut;

    @BeforeEach
    public void setup() {
        uut = new RecipeIngredientHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void insertingWorks() {
        RecipeIngredientWithIdForInsertion data = RecipeIngredientWithIdForInsertion.builder()
                .amount(5)
                .ingredient(3)
                .unit(1)
                .recipe(4)
                .build();

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertInsertableIsInserted(result, data, 2, 2);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(true, Instant.EPOCH);

        BitemporalRecipeIngredient sample = (BitemporalRecipeIngredient) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalRecipeIngredient> data = result.success()
                .map(v -> (BitemporalRecipeIngredient) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.id() == 1 &&
                        l.version() == 0 &&
                        l.amount() == 2 &&
                        l.ingredient() == 3 &&
                        l.recipe() == 1 &&
                        l.unit() == 2 &&
                        l.initiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<RecipeIngredient> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.amount() == 2 &&
                        l.ingredient() == 3 &&
                        l.recipe() == 1 &&
                        l.unit() == 2));
    }

    @Test
    public void deletingWorks() {
        RecipeIngredientForDeletion input = RecipeIngredientForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<RecipeIngredient>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }

    @Test
    public void checkingSetEqualityWorks() {
        RecipeIngredientForDeletion ingredient = RecipeIngredientForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Versionable<Recipe> recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Set<Versionable<RecipeIngredient>> ingredients = Set.of(ingredient);

        StatusCode result = uut.areEntitiesComplete(recipe, ingredients);

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void moreIngredientsThanInRecipeAreRejected() {
        RecipeIngredientForDeletion ingredient1 = RecipeIngredientForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        RecipeIngredientForDeletion ingredient2 = RecipeIngredientForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        Versionable<Recipe> recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Set<Versionable<RecipeIngredient>> ingredients = Set.of(ingredient1, ingredient2);

        StatusCode result = uut.areEntitiesComplete(recipe, ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void differentVersionIsRejected() {
        RecipeIngredientForDeletion ingredient = RecipeIngredientForDeletion.builder()
                .id(1)
                .version(1)
                .build();
        Identifiable<Recipe> recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Set<Versionable<RecipeIngredient>> ingredients = Set.of(ingredient);

        StatusCode result = uut.areEntitiesComplete(recipe, ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void differentIdIsRejected() {
        RecipeIngredientForDeletion ingredient = RecipeIngredientForDeletion.builder()
                .id(2)
                .version(0)
                .build();
        Identifiable<Recipe> recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Set<Versionable<RecipeIngredient>> ingredients = Set.of(ingredient);

        StatusCode result = uut.areEntitiesComplete(recipe, ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }


    @Test
    public void lessIngredientsThanInRecipeAreRejected() {
        Identifiable<Recipe> recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        Set<Versionable<RecipeIngredient>> ingredients = Set.of();

        StatusCode result = uut.areEntitiesComplete(recipe, ingredients);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void deletingAllOfRecipeWorks() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.deleteAllOf(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<RecipeIngredient>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(0, stream.success().count());
    }

    @Test
    public void deletingAllOfAbsentRecipeWorks() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(2)
                .version(0)
                .build();

        StatusCode result = uut.deleteAllOf(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, Stream<RecipeIngredient>> stream = uut.get(false, Instant.EPOCH);
        assertTrue(stream.isSuccess());
        assertEquals(1, stream.success().count());
    }

    @Override
    public CrudDatabaseHandler<RecipeIngredientRecord, RecipeIngredient> getDbHandler() {
        return uut;
    }
}
