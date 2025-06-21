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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.common.api.StatusCode.INVALID_DATA_VERSION;
import static de.njsm.stocks.common.api.StatusCode.NOT_FOUND;
import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeIngredientHandlerTest
        extends DbTestCase
        implements CrudOperationsTest<RecipeIngredientRecord, RecipeIngredient>,
                CompleteEntityReferenceCheckerTest<Recipe, RecipeIngredient>,
                EditingTest<RecipeIngredientRecord, RecipeIngredient> {

    private RecipeIngredientHandler uut;

    @BeforeEach
    public void setup() {
        uut = new RecipeIngredientHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }

    @Override
    public RecipeIngredientWithIdForInsertion getInsertable() {
        return RecipeIngredientWithIdForInsertion.builder()
                .amount(5)
                .ingredient(3)
                .unit(1)
                .recipe(1)
                .build();
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        BitemporalRecipeIngredient sample = (BitemporalRecipeIngredient) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<RecipeIngredient>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

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
    void editingMissingIsRejected() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .amount(2)
                .ingredient(3)
                .recipe(4)
                .unit(5)
                .build();

        StatusCode result = uut.edit(data);

        assertThat(result, is(NOT_FOUND));
    }

    @Test
    void editingWrongVersionIsRejected() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(1)
                .amount(2)
                .ingredient(3)
                .recipe(4)
                .unit(5)
                .build();

        StatusCode result = uut.edit(data);

        assertThat(result, is(INVALID_DATA_VERSION));
    }

    @Test
    void editingWithoutChangeWorksWithoutChange() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .ingredient(3)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditedDataIsPresentWithoutUpdate(data, result);
    }

    @Test
    void editingAmountWorks() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(3)
                .ingredient(3)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingIngredientWorks() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .ingredient(2)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingRecipeWorks() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .ingredient(3)
                .recipe(2)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingUnitWorks() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .ingredient(3)
                .recipe(1)
                .unit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void fullEditingUnitWorks() {
        RecipeIngredientForEditing data = RecipeIngredientForEditing.builder()
                .id(1)
                .version(0)
                .amount(3)
                .ingredient(2)
                .recipe(2)
                .unit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void deletingAllOfRecipeWorks() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.deleteAllOf(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        assertEquals(0, getCurrentData().size());
    }

    @Test
    public void deletingAllOfAbsentRecipeWorks() {
        RecipeForDeletion recipe = RecipeForDeletion.builder()
                .id(2)
                .version(0)
                .build();

        StatusCode result = uut.deleteAllOf(recipe);

        assertEquals(StatusCode.SUCCESS, result);
        assertEquals(1, getCurrentData().size());
    }

    @Override
    public RecipeIngredientHandler getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 1;
    }

    @Override
    public RecipeIngredientHandler getUnitUnderTest() {
        return getDbHandler();
    }

    @Override
    public Versionable<Recipe> getPrimary() {
        return RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeIngredient> getValidForeign() {
        return RecipeIngredientForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeIngredient> getUnreferencedForeign() {
        return RecipeIngredientForDeletion.builder()
                .id(2)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeIngredient> getWrongVersionForeign() {
        return RecipeIngredientForDeletion.builder()
                .id(1)
                .version(1)
                .build();
    }

    @Override
    public Versionable<RecipeIngredient> getWrongIdForeign() {
        return RecipeIngredientForDeletion.builder()
                .id(2)
                .version(0)
                .build();
    }

    @Override
    public RecipeIngredientForDeletion getUnknownEntity() {
        return RecipeIngredientForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public RecipeIngredientForDeletion getWrongVersionEntity() {
        return RecipeIngredientForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public RecipeIngredientForDeletion getValidEntity() {
        return RecipeIngredientForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }
}
