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
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE;


public class RecipeHandler extends CrudDatabaseHandler<RecipeRecord, Recipe> {


    public RecipeHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode edit(RecipeForEditing recipe) {
        return runCommand(context -> checkPresenceInThisVersion(recipe, context)
                .bind(() ->
                        currentUpdate(context, List.of(
                                    RECIPE.ID,
                                    RECIPE.VERSION.add(1),
                                    DSL.inline(recipe.name()),
                                    DSL.inline(recipe.instructions()),
                                    DSL.inline(recipe.duration())
                            ),
                            RECIPE.ID.eq(recipe.id())
                                    .and(RECIPE.VERSION.eq(recipe.version()))
                                    .and(
                                            RECIPE.NAME.ne(recipe.name())
                                                    .or(RECIPE.INSTRUCTIONS.ne(recipe.instructions()))
                                                    .or(RECIPE.DURATION.ne(recipe.duration()))
                                    )
                        ).map(this::notFoundIsOk)
                )
        );
    }

    @Override
    protected Table<RecipeRecord> getTable() {
        return RECIPE;
    }

    @Override
    protected TableField<RecipeRecord, Integer> getIdField() {
        return RECIPE.ID;
    }

    @Override
    protected TableField<RecipeRecord, Integer> getVersionField() {
        return RECIPE.VERSION;
    }

    @Override
    protected Function<RecipeRecord, Recipe> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalRecipe.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .name(cursor.getName())
                    .instructions(cursor.getInstructions())
                    .duration(cursor.getDuration())
                    .build();
        else
            return cursor -> RecipeForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .name(cursor.getName())
                    .instructions(cursor.getInstructions())
                    .duration(cursor.getDuration())
                    .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                RECIPE.ID,
                RECIPE.VERSION,
                RECIPE.NAME,
                RECIPE.INSTRUCTIONS,
                RECIPE.DURATION
        );
    }
}
