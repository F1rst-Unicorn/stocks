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
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.FoodForDeletion;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
abstract class FoodDao {

    @Query("select * " +
            "from current_food")
    abstract List<FoodDbEntity> getAll();

    @Query("select id, name, to_buy as toBuy " +
            "from current_food " +
            "where id not in (" +
            "   select of_type " +
            "   from current_food_item" +
            ") " +
            "order by name")
    abstract Observable<List<EmptyFood>> getCurrentEmptyFood();

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract FoodForDeletion getForDeletion(int id);
}
