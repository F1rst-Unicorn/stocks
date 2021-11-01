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
import de.njsm.stocks.android.db.entities.RecipeIngredient;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.views.RecipeItemWithCurrentStock;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_FIELDS_QUALIFIED;
import static de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_TABLE;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class RecipeIngredientDao implements Inserter<RecipeIngredient> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<RecipeIngredient> data);

    @Transaction
    public void synchronise(List<RecipeIngredient> data) {
        delete();
        insert(data);
    }

    @Query("delete from recipe_ingredient")
    abstract void delete();

    public LiveData<List<RecipeIngredient>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    public List<RecipeIngredient> getIngredientsOf(int recipeId) {
        return getIngredientsOf(recipeId, DATABASE_INFINITY);
    }

    public LiveData<List<RecipeIngredient>> getLiveIngredientsOf(int recipeId) {
        return getLiveIngredientsOf(recipeId, DATABASE_INFINITY);
    }

    public LiveData<List<RecipeItemWithCurrentStock.SingleRecipeItemWithCurrentStock>> getIngredientViewsOf(int recipeId) {
        return getIngredientViewsOf(recipeId, DATABASE_INFINITY);
    }

    @Query("select * " +
            "from recipe_ingredient " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<RecipeIngredient>> getAll(Instant infinity);

    @Query("select * " +
            "from recipe_ingredient " +
            "where recipe = :recipeId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract List<RecipeIngredient> getIngredientsOf(int recipeId, Instant infinity);

    @Query("select * " +
            "from recipe_ingredient " +
            "where recipe = :recipeId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<RecipeIngredient>> getLiveIngredientsOf(int recipeId, Instant infinity);

    @Query("select " +
            Sql.FOOD_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.RECIPE_INGREDIENT_FIELDS +
            SCALED_AMOUNT_FIELDS_QUALIFIED +
            "1 from recipe_ingredient recipe_ingredient " +
            Sql.FOOD_JOIN_RECIPE_INGREDIENT +
            Sql.SCALED_UNIT_JOIN_RECIPE_INGREDIENT +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "join " + SCALED_AMOUNT_TABLE + " " + SCALED_AMOUNT_TABLE + " on recipe_ingredient.ingredient = current_scaled_amount.of_type " +
            "where recipe_ingredient.recipe = :recipeId " +
            "and recipe_ingredient.valid_time_start <= " + NOW +
            "and " + NOW + " < recipe_ingredient.valid_time_end " +
            "and recipe_ingredient.transaction_time_end = :infinity " +
            "order by food_name, unit_name")
    abstract LiveData<List<RecipeItemWithCurrentStock.SingleRecipeItemWithCurrentStock>> getIngredientViewsOf(int recipeId, Instant infinity);
}
