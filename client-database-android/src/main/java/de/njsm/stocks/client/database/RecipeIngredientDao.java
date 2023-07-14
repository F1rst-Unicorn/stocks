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
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.util.List;

@Dao
abstract class RecipeIngredientDao {

    @Query("select * " +
            "from current_recipe_ingredient")
    abstract List<RecipeIngredientDbEntity> getAll();

    @Query("select id, version " +
            "from current_recipe_ingredient " +
            "where recipe = :recipeId")
    abstract List<VersionedId> getIngredientsForDeletionOf(int recipeId);

    @Query("select id, version " +
            "from current_recipe_ingredient " +
            "where id = :id")
    abstract VersionedId getIngredientVersion(int id);

    @Query("select * " +
            "from current_recipe_ingredient " +
            "where recipe = :recipeId " +
            "order by id")
    abstract Maybe<List<RecipeIngredientDbEntity>> getIngredientsOf(int recipeId);

    @Query("select " +
                "f.id as food, " +
                "f.name as foodName, " +
                "f.to_buy as toBuy ," +
                "u.id as unit, " +
                "u.abbreviation as abbreviation, " +
                "s.scale as scale," +
                "i.amount as amount " +
            "from current_recipe_ingredient i " +
            "join current_food f on f.id = i.ingredient " +
            "join current_scaled_unit s on s.id = i.unit " +
            "join current_unit u on u.id = s.unit " +
            "where i.recipe = :recipeId " +
            "order by f.name")
    abstract Observable<List<RecipeIngredientForCookingData>> getCookingIngredientsOf(int recipeId);

    static class RecipeIngredientForCookingData {
        int food;
        String foodName;
        boolean toBuy;
        int unit;
        String abbreviation;
        BigDecimal scale;
        int amount;
    }

    @Query("select " +
            "fi.of_type as food, " +
            "u.id as unit, " +
            "u.abbreviation as abbreviation, " +
            "s.id as scaledUnit, " +
            "s.scale as scale, " +
            "count(*) as presentCount " +
            "from current_food_item fi " +
            "join current_scaled_unit s on s.id = fi.unit " +
            "join current_unit u on u.id = s.unit " +
            "where fi.of_type in (" +
                "select ingredient " +
                "from current_recipe_ingredient i " +
                "where i.recipe = :recipeId " +
            ") " +
            "group by fi.of_type, u.id, u.abbreviation, s.id, s.scale")
    abstract Observable<List<RecipeIngredientPresentData>> getPresentCookingIngredientsOf(int recipeId);

    static class RecipeIngredientPresentData {
        int food;
        int unit;
        String abbreviation;
        int scaledUnit;
        BigDecimal scale;
        int presentCount;
    }
}
