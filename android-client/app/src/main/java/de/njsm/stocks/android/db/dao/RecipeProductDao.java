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

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
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
}
