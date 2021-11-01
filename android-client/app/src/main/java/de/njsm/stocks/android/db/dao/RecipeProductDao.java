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

package de.njsm.stocks.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import de.njsm.stocks.android.db.entities.RecipeProduct;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.views.RecipeItemWithCurrentStock;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_FIELDS_QUALIFIED;
import static de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_TABLE;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class RecipeProductDao implements Inserter<RecipeProduct> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<RecipeProduct> data);

    @Transaction
    public void synchronise(List<RecipeProduct> data) {
        delete();
        insert(data);
    }

    @Query("delete from recipe_product")
    abstract void delete();

    public LiveData<List<RecipeProduct>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    public List<RecipeProduct> getProductsOf(int recipeId) {
        return getProductsOf(recipeId, DATABASE_INFINITY);
    }

    public LiveData<List<RecipeProduct>> getLiveProductsOf(int recipeId) {
        return getLiveProductsOf(recipeId, DATABASE_INFINITY);
    }

    public LiveData<List<RecipeItemWithCurrentStock.SingleRecipeItemWithCurrentStock>> getProductViewsOf(int recipeId) {
        return getProductViewsOf(recipeId, DATABASE_INFINITY);
    }

    @Query("select * " +
            "from recipe_product " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<RecipeProduct>> getAll(Instant infinity);

    @Query("select * " +
            "from recipe_product " +
            "where recipe = :recipeId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract List<RecipeProduct> getProductsOf(int recipeId, Instant infinity);

    @Query("select * " +
            "from recipe_product " +
            "where recipe = :recipeId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<RecipeProduct>> getLiveProductsOf(int recipeId, Instant infinity);

    @Query("select " +
            Sql.FOOD_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.RECIPE_PRODUCT_FIELDS +
            SCALED_AMOUNT_FIELDS_QUALIFIED +
            "1 from recipe_product recipe_product " +
            Sql.FOOD_JOIN_RECIPE_PRODUCT +
            Sql.SCALED_UNIT_JOIN_RECIPE_PRODUCT +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "join " + SCALED_AMOUNT_TABLE + " " + SCALED_AMOUNT_TABLE + " on recipe_product.product = current_scaled_amount.of_type " +
            "where recipe_product.recipe = :recipeId " +
            "and recipe_product.valid_time_start <= " + NOW +
            "and " + NOW + " < recipe_product.valid_time_end " +
            "and recipe_product.transaction_time_end = :infinity " +
            "order by food_name, unit_name")
    abstract LiveData<List<RecipeItemWithCurrentStock.SingleRecipeItemWithCurrentStock>> getProductViewsOf(int recipeId, Instant infinity);

}
