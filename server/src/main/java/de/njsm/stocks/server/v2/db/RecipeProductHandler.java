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
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_PRODUCT;


public class RecipeProductHandler extends CrudDatabaseHandler<RecipeProductRecord, RecipeProduct> implements CompleteReferenceChecker<Recipe, RecipeProduct> {


    public RecipeProductHandler(ConnectionFactory connectionFactory,
                                String resourceIdentifier,
                                int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    public StatusCode areEntitiesComplete(Identifiable<Recipe> recipe, Set<? extends Versionable<RecipeProduct>> products) {
        return runCommand(context -> new CompleteEntityReferenceChecker<Recipe, RecipeProduct, RecipeProductRecord>(
                getIdField(),
                getVersionField(),
                RECIPE_PRODUCT.VALID_TIME_START,
                RECIPE_PRODUCT.VALID_TIME_END,
                RECIPE_PRODUCT.TRANSACTION_TIME_END,
                RECIPE_PRODUCT.RECIPE,
                getTable()
        ).check(context, recipe, products));
    }

    public StatusCode edit(RecipeProductForEditing data) {
        return runCommand(context -> checkPresenceInThisVersion(data, context)
                .bind(() -> currentUpdate(context, List.of(
                            RECIPE_PRODUCT.ID,
                            RECIPE_PRODUCT.VERSION.add(1),
                            DSL.inline(data.amount()),
                            DSL.inline(data.product()),
                            DSL.inline(data.recipe()),
                            DSL.inline(data.unit())
                    ),
                    RECIPE_PRODUCT.ID.eq(data.id())
                            .and(RECIPE_PRODUCT.VERSION.eq(data.version()))
                            .and(RECIPE_PRODUCT.AMOUNT.ne(data.amount())
                                    .or(RECIPE_PRODUCT.RECIPE.ne(data.recipe()))
                                    .or(RECIPE_PRODUCT.PRODUCT.ne(data.product()))
                                    .or(RECIPE_PRODUCT.UNIT.ne(data.unit())))
            )
                    .map(this::notFoundIsOk))
        );
    }



    public StatusCode deleteAllOf(RecipeForDeletion recipe) {
        return currentDelete(RECIPE_PRODUCT.RECIPE.eq(recipe.id()))
                .map(this::notFoundIsOk);
    }

    @Override
    protected Table<RecipeProductRecord> getTable() {
        return RECIPE_PRODUCT;
    }

    @Override
    protected TableField<RecipeProductRecord, Integer> getIdField() {
        return RECIPE_PRODUCT.ID;
    }

    @Override
    protected TableField<RecipeProductRecord, Integer> getVersionField() {
        return RECIPE_PRODUCT.VERSION;
    }

    @Override
    protected Function<RecipeProductRecord, RecipeProduct> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalRecipeProduct.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .amount(cursor.getAmount())
                    .product(cursor.getProduct())
                    .recipe(cursor.getRecipe())
                    .unit(cursor.getUnit())
                    .build();
        else
            return cursor -> RecipeProductForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .amount(cursor.getAmount())
                    .product(cursor.getProduct())
                    .recipe(cursor.getRecipe())
                    .unit(cursor.getUnit())
                    .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                RECIPE_PRODUCT.ID,
                RECIPE_PRODUCT.VERSION,
                RECIPE_PRODUCT.AMOUNT,
                RECIPE_PRODUCT.PRODUCT,
                RECIPE_PRODUCT.RECIPE,
                RECIPE_PRODUCT.UNIT
        );
    }
}
