/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.database;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.util.List;

@Dao
abstract class RecipeProductDao {

    @Query("select * " +
            "from current_recipe_product")
    abstract List<RecipeProductDbEntity> getAll();

    @Query("select id, version " +
            "from current_recipe_product " +
            "where recipe = :recipeId")
    abstract List<VersionedId> getProductsForDeletionOf(int recipeId);

    @Query("select id, version " +
            "from current_recipe_product " +
            "where id = :id")
    abstract VersionedId getProductVersion(int id);

    @Query("select * " +
            "from current_recipe_product " +
            "where recipe = :recipeId " +
            "order by id")
    abstract Maybe<List<RecipeProductDbEntity>> getProductsOf(int recipeId);

    @Query("select " +
                "f.id as product, " +
                "s.id as unit, " +
                "p.amount as amount, " +
                "f.name as name, " +
                "s.scale as scale, " +
                "u.abbreviation as abbreviation " +
            "from current_recipe_product p " +
            "join current_food f on f.id = p.product " +
            "join current_scaled_unit s on s.id = p.unit " +
            "join current_unit u on u.id = s.unit " +
            "where recipe = :recipeId " +
            "order by f.name")
    abstract Observable<List<RecipeProductForCooking>> getProductsBy(int recipeId);

    static class RecipeProductForCooking {
        int product;
        int unit;
        int amount;
        String name;
        BigDecimal scale;
        String abbreviation;
    }

}
