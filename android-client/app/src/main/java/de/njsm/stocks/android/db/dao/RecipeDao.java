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
import de.njsm.stocks.android.db.entities.Recipe;

import java.time.Instant;

import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class RecipeDao implements Inserter<Recipe> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Recipe> recipes);

    public LiveData<List<Recipe>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    @Transaction
    public void synchronise(List<Recipe> data) {
        delete();
        insert(data);
    }

    public LiveData<Recipe> getRecipe(int recipeId) {
        return getRecipe(recipeId, DATABASE_INFINITY);
    }

    @Query("delete from recipe")
    abstract void delete();

    @Query("select * " +
            "from recipe " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by name")
    abstract LiveData<List<Recipe>> getAll(Instant infinity);

    @Query("select * " +
            "from recipe " +
            "where _id = :recipeId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<Recipe> getRecipe(int recipeId, Instant infinity);
}
