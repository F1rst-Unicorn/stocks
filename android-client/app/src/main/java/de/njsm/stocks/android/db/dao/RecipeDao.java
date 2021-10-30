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
import de.njsm.stocks.android.db.views.RecipeWithRating;

import java.util.List;

@Dao
public abstract class RecipeDao implements Inserter<Recipe> {

    private static final String GET_ALL_WITH_RATING =
            "select recipe.*, " +
            "coalesce(nb.necessary_to_cook, 0) as necessity_rating, " +
            "coalesce(sb.sufficient_to_cook, 0) as sufficiency_rating " +
            "from current_recipe recipe " +
            "left outer join (select recipe_id, " +
                    "cast(7 * avg(present_for_sufficient) as int) as sufficient_to_cook " +
                    "from recipe_stock_rating_base " +
                    "group by recipe_id) sb on recipe._id = sb.recipe_id " +
            "left outer join (select recipe_id, " +
                    "cast(7 * avg(present_for_necessary) as int) as necessary_to_cook " +
                    "from recipe_stock_rating_base " +
                    "group by recipe_id) nb on nb.recipe_id = recipe._id ";

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Recipe> recipes);

    @Transaction
    public void synchronise(List<Recipe> data) {
        delete();
        insert(data);
    }

    @Query(GET_ALL_WITH_RATING +
            "order by recipe.name")
    public abstract LiveData<List<RecipeWithRating>> getAllWithRating();

    @Query("select * " +
            "from current_recipe " +
            "order by name")
    @Override
    public abstract LiveData<List<Recipe>> getAll();

    @Query("delete from recipe")
    abstract void delete();

    @Query(GET_ALL_WITH_RATING +
            "order by necessary_to_cook desc, sb.sufficient_to_cook desc, recipe.name")
    public abstract LiveData<List<RecipeWithRating>> getByCookability();

    @Query("select * " +
            "from current_recipe " +
            "where _id = :recipeId ")
    public abstract LiveData<Recipe> getRecipe(int recipeId);
}
