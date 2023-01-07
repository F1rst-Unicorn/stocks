/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.client.database;

import androidx.room.Dao;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.RecipeForListingBaseData;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
abstract class RecipeDao {

    @Query("select * " +
            "from current_recipe")
    abstract List<RecipeDbEntity> getAll();

    @Query("select * " +
            "from current_recipe " +
            "order by name")
    abstract Observable<List<RecipeForListingBaseData>> getRecipes();

    @Query("select i.recipe, i.id as ingredient, u.id as unit, s.scale as scale, count(*) as amount " +
            "from current_recipe_ingredient i " +
            "join current_food_item f on f.of_type = i.ingredient " +
            "join current_scaled_unit s on f.unit = s.id " +
            "join current_unit u on u.id = s.unit " +
            "group by i.recipe, i.id, u.id, s.scale " +
            "order by i.id")
    abstract Observable<List<RecipeListRepositoryImpl.RecipeIngredientAmountBaseData>> getIngredientsPresentAmounts();

    @Query("select i.recipe, i.id as ingredient, u.id as unit, s.scale, i.amount " +
            "from current_recipe_ingredient i " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on u.id = s.unit " +
            "order by i.id")
    abstract Observable<List<RecipeListRepositoryImpl.RecipeIngredientAmountBaseData>> getIngredientsRequiredAmount();
}
