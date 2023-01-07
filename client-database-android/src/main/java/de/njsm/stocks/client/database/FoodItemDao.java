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
import de.njsm.stocks.client.business.entities.FoodItemForDeletion;
import de.njsm.stocks.client.business.entities.FoodItemForEditing;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;

@Dao
abstract class FoodItemDao {

    @Query("select * " +
            "from current_food_item")
    abstract List<FoodItemDbEntity> getAll();

    @Query("select max(eat_by) " +
            "from current_food_item " +
            "where of_type = :foodId")
    abstract Maybe<Instant> getMaxEatByOfPresentItemsOf(int foodId);

    @Query("select max(i.eat_by) " +
            "from food_item i " +
            "where i.of_type = :foodId " +
            "and i.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL + " " +
            "and i.version = (select max(i2.version) from food_item i2 where i2.id = i.id)")
    abstract Maybe<Instant> getMaxEatByEverOf(int foodId);

    @Query("select " +
            "l.id " +
            "from current_location l " +
            "join current_food_item i on i.stored_in = l.id " +
            "where i.of_type = :foodId " +
            "group by l.id " +
            "order by count(*) desc " +
            "limit 1")
    abstract Maybe<Integer> getLocationWithMostItemsOfType(int foodId);

    @Query("select * " +
            "from current_food_item " +
            "where id = :id")
    abstract FoodItemForDeletion getVersionOf(int id);

    @Query("select i.id, f.id as ofType, f.name as foodName, i.eat_by as eatBy, i.stored_in as storedIn, i.unit " +
            "from current_food_item i " +
            "join current_food f on f.id = i.of_type " +
            "where i.id = :id")
    abstract Observable<FoodItemEditRepositoryImpl.FoodItemEditRecord> getItemToEdit(int id);

    @Query("select id, version, eat_by as eatBy, stored_in as storedIn, unit " +
            "from current_food_item " +
            "where id = :id")
    abstract FoodItemForEditing getCurrentItemForEditing(int id);
}
