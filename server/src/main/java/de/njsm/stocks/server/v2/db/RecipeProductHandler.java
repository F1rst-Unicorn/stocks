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
import de.njsm.stocks.server.v2.business.data.BitemporalRecipeProduct;
import de.njsm.stocks.server.v2.business.data.RecipeForDeletion;
import de.njsm.stocks.server.v2.business.data.RecipeProduct;
import de.njsm.stocks.server.v2.business.data.RecipeProductForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE_PRODUCT;


public class RecipeProductHandler extends CrudDatabaseHandler<RecipeProductRecord, RecipeProduct> {


    public RecipeProductHandler(ConnectionFactory connectionFactory,
                                String resourceIdentifier,
                                int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    protected Table<RecipeProductRecord> getTable() {
        return RECIPE_PRODUCT;
    }

    public StatusCode deleteAllOf(RecipeForDeletion recipe) {
        return currentDelete(RECIPE_PRODUCT.RECIPE.eq(recipe.getId()))
                .map(this::notFoundIsOk);
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
            return cursor -> new BitemporalRecipeProduct(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getAmount(),
                    cursor.getProduct(),
                    cursor.getRecipe(),
                    cursor.getUnit()
            );
        else
            return cursor -> new RecipeProductForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getAmount(),
                    cursor.getProduct(),
                    cursor.getRecipe(),
                    cursor.getUnit()
            );
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
