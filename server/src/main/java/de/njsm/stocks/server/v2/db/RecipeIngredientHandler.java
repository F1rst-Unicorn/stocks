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
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_INGREDIENT;


public class RecipeIngredientHandler
        extends CrudDatabaseHandler<RecipeIngredientRecord, RecipeIngredient>
        implements CompleteReferenceChecker<Recipe, RecipeIngredient> {


    public RecipeIngredientHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public StatusCode areEntitiesComplete(Identifiable<Recipe> recipe, Set<? extends Versionable<RecipeIngredient>> ingredients) {
        return runCommand(context -> new CompleteEntityReferenceChecker<Recipe, RecipeIngredient, RecipeIngredientRecord>(
                getIdField(),
                getVersionField(),
                RECIPE_INGREDIENT.VALID_TIME_START,
                RECIPE_INGREDIENT.VALID_TIME_END,
                RECIPE_INGREDIENT.TRANSACTION_TIME_END,
                RECIPE_INGREDIENT.RECIPE,
                getTable()
        ).check(context, recipe, ingredients));
    }

    public StatusCode edit(RecipeIngredientForEditing data) {
        return runCommand(context -> checkPresenceInThisVersion(data, context)
                .bind(() ->
                        currentUpdate(context, List.of(
                                RECIPE_INGREDIENT.ID,
                                RECIPE_INGREDIENT.VERSION.add(1),
                                DSL.inline(data.amount()),
                                DSL.inline(data.ingredient()),
                                DSL.inline(data.recipe()),
                                DSL.inline(data.unit())
                                ),
                                RECIPE_INGREDIENT.ID.eq(data.id())
                                        .and(RECIPE_INGREDIENT.VERSION.eq(data.version()))
                                        .and(RECIPE_INGREDIENT.AMOUNT.ne(data.amount())
                                                .or(RECIPE_INGREDIENT.RECIPE.ne(data.recipe()))
                                                .or(RECIPE_INGREDIENT.INGREDIENT.ne(data.ingredient()))
                                                .or(RECIPE_INGREDIENT.UNIT.ne(data.unit())))
                        ).map(this::notFoundIsOk)
                ));
    }

    public StatusCode deleteAllOf(RecipeForDeletion recipe) {
        return currentDelete(RECIPE_INGREDIENT.RECIPE.eq(recipe.id()))
                .map(this::notFoundIsOk);
    }

    @Override
    protected Table<RecipeIngredientRecord> getTable() {
        return RECIPE_INGREDIENT;
    }

    @Override
    protected TableField<RecipeIngredientRecord, Integer> getIdField() {
        return RECIPE_INGREDIENT.ID;
    }

    @Override
    protected TableField<RecipeIngredientRecord, Integer> getVersionField() {
        return RECIPE_INGREDIENT.VERSION;
    }

    @Override
    protected Function<RecipeIngredientRecord, RecipeIngredient> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalRecipeIngredient.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .amount(cursor.getAmount())
                    .ingredient(cursor.getIngredient())
                    .recipe(cursor.getRecipe())
                    .unit(cursor.getUnit())
                    .build();
        else
            return cursor -> RecipeIngredientForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .amount(cursor.getAmount())
                    .ingredient(cursor.getIngredient())
                    .recipe(cursor.getRecipe())
                    .unit(cursor.getUnit())
                    .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                RECIPE_INGREDIENT.ID,
                RECIPE_INGREDIENT.VERSION,
                RECIPE_INGREDIENT.AMOUNT,
                RECIPE_INGREDIENT.INGREDIENT,
                RECIPE_INGREDIENT.RECIPE,
                RECIPE_INGREDIENT.UNIT
        );
    }
}
