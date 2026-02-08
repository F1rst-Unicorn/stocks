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
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.common.api.StatusCode.INVALID_DATA_VERSION;
import static de.njsm.stocks.common.api.StatusCode.NOT_FOUND;
import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeProductHandlerTest extends DbTestCase
        implements CrudOperationsTest<RecipeProductRecord, RecipeProduct>, CompleteEntityReferenceCheckerTest<Recipe, RecipeProduct> {

    private RecipeProductHandler uut;

    @BeforeEach
    public void setup() {
        uut = new RecipeProductHandler(getConnectionFactory());
    }

    @Override
    public RecipeProductWithIdForInsertion getInsertable() {
        return RecipeProductWithIdForInsertion.builder()
                .amount(5)
                .product(3)
                .unit(1)
                .recipe(1)
                .build();
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, List<RecipeProduct>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        BitemporalRecipeProduct sample = (BitemporalRecipeProduct) result.success().stream().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, List<RecipeProduct>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        assertTrue(result.isSuccess());
        List<BitemporalRecipeProduct> data = result.success()
                .stream()
                .map(v -> (BitemporalRecipeProduct) v).toList();

        assertTrue(data.stream().anyMatch(l ->
                        l.id() == 1 &&
                        l.version() == 0 &&
                        l.amount() == 2 &&
                        l.product() == 3 &&
                        l.recipe() == 1 &&
                        l.unit() == 2 &&
                        l.initiates() == 1));
    }

    @Test
    void editingMissingIsRejected() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .amount(2)
                .product(3)
                .recipe(4)
                .unit(5)
                .build();

        StatusCode result = uut.edit(data);

        assertThat(result, is(NOT_FOUND));
    }

    @Test
    void editingWrongVersionIsRejected() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(1)
                .amount(2)
                .product(3)
                .recipe(4)
                .unit(5)
                .build();

        StatusCode result = uut.edit(data);

        assertThat(result, is(INVALID_DATA_VERSION));
    }

    @Test
    void editingWithoutChangeWorksWithoutChange() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .product(3)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditedDataIsPresentWithoutUpdate(data, result);
    }

    @Test
    void editingAmountWorks() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(3)
                .product(3)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingIngredientWorks() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .product(2)
                .recipe(1)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingRecipeWorks() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .product(3)
                .recipe(2)
                .unit(2)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void editingUnitWorks() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(2)
                .product(3)
                .recipe(1)
                .unit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    void fullEditingUnitWorks() {
        RecipeProductForEditing data = RecipeProductForEditing.builder()
                .id(1)
                .version(0)
                .amount(3)
                .product(2)
                .recipe(2)
                .unit(1)
                .build();

        StatusCode result = uut.edit(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void deletingWorks() {
        RecipeProductForDeletion input = RecipeProductForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.delete(input);

        assertEquals(StatusCode.SUCCESS, result);
        assertEquals(0, getCurrentData().size());
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
    public RecipeProductHandler getDbHandler() {
        return getUnitUnderTest();
    }

    @Override
    public int getNumberOfEntities() {
        return 1;
    }

    @Override
    public RecipeProductHandler getUnitUnderTest() {
        return uut;
    }

    @Override
    public Versionable<Recipe> getPrimary() {
        return RecipeForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getValidForeign() {
        return RecipeProductForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getUnreferencedForeign() {
        return RecipeProductForDeletion.builder()
                .id(2)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getWrongVersionForeign() {
        return RecipeProductForDeletion.builder()
                .id(1)
                .version(1)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getWrongIdForeign() {
        return RecipeProductForDeletion.builder()
                .id(2)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getUnknownEntity() {
        return RecipeProductForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getWrongVersionEntity() {
        return RecipeProductForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<RecipeProduct> getValidEntity() {
        return RecipeProductForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }
}
