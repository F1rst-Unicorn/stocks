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

import de.njsm.stocks.common.api.Recipe;
import de.njsm.stocks.common.api.impl.BitemporalRecipe;
import de.njsm.stocks.common.api.impl.RecipeForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE;


public class RecipeHandler extends CrudDatabaseHandler<RecipeRecord, Recipe> {


    public RecipeHandler(ConnectionFactory connectionFactory,
                         String resourceIdentifier,
                         int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
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
            return cursor -> new BitemporalRecipe(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getName(),
                    cursor.getInstructions(),
                    cursor.getDuration()
            );
        else
            return cursor -> new RecipeForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getName(),
                    cursor.getInstructions(),
                    cursor.getDuration()
            );
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
