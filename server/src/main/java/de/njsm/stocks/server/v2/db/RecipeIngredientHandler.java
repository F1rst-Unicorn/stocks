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

import de.njsm.stocks.server.v2.business.data.BitemporalRecipeIngredient;
import de.njsm.stocks.server.v2.business.data.RecipeIngredient;
import de.njsm.stocks.server.v2.business.data.RecipeIngredientForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_INGREDIENT;


public class RecipeIngredientHandler extends CrudDatabaseHandler<RecipeIngredientRecord, RecipeIngredient> {


    public RecipeIngredientHandler(ConnectionFactory connectionFactory,
                                   String resourceIdentifier,
                                   int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
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
            return cursor -> new BitemporalRecipeIngredient(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getAmount(),
                    cursor.getIngredient(),
                    cursor.getRecipe(),
                    cursor.getUnit()
            );
        else
            return cursor -> new RecipeIngredientForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getAmount(),
                    cursor.getIngredient(),
                    cursor.getRecipe(),
                    cursor.getUnit()
            );
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
