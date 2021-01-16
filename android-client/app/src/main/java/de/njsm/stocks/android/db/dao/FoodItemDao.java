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
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.threeten.bp.Instant;

import java.util.List;

import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.db.views.FoodItemView;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FoodItem[] food);

    @Transaction
    public void synchronise(FoodItem[] food) {
        delete();
        insert(food);
    }

    public LiveData<Instant> getLatestExpirationOf(int foodType) {
        return getLatestExpirationOf(foodType, DATABASE_INFINITY);
    }

    public LiveData<List<FoodItemView>> getItemsOfType(int foodId) {
        return getItemsOfType(foodId, DATABASE_INFINITY);
    }

    public LiveData<FoodItemView> getItem(int id) {
        return getItem(id, DATABASE_INFINITY);
    }

    public LiveData<Integer> countItemsOfType(int foodId) {
        return countItemsOfType(foodId, DATABASE_INFINITY);
    }


    @Query("select i._id as _id, i.version as version, u.name as userName, " +
            "d.name as deviceName, l.name as location, i.eat_by as eatByDate, i.valid_time_start," +
            "i.valid_time_end, i.transaction_time_start, i.transaction_time_end, i.initiates, " +
            "i.of_type as ofType, i.stored_in as storedIn " +
            "from FoodItem i " +
            "inner join User u on i.buys = u._id " +
            "inner join User_device d on i.registers = d._id " +
            "inner join Location l on i.stored_in = l._id " +
            "where i.of_type = :foodId " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "and u.valid_time_start <= " + NOW +
            "and " + NOW + " < u.valid_time_end " +
            "and u.transaction_time_end = :infinity " +
            "and d.valid_time_start <= " + NOW +
            "and " + NOW + " < d.valid_time_end " +
            "and d.transaction_time_end = :infinity " +
            "and l.valid_time_start <= " + NOW +
            "and " + NOW + " < l.valid_time_end " +
            "and l.transaction_time_end = :infinity " +
            "order by i.eat_by")
    abstract LiveData<List<FoodItemView>> getItemsOfType(int foodId, Instant infinity);

    @Query("select i._id as _id, i.version as version, u.name as userName, " +
            "d.name as deviceName, l.name as location, i.eat_by as eatByDate, " +
            "i.of_type as ofType, i.stored_in as storedIn, i.valid_time_start," +
            "i.valid_time_end, i.transaction_time_start, i.transaction_time_end, i.initiates " +
            "from FoodItem i " +
            "inner join User u on i.buys = u._id " +
            "inner join User_device d on i.registers = d._id " +
            "inner join Location l on i.stored_in = l._id " +
            "where i._id = :id " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "and u.valid_time_start <= " + NOW +
            "and " + NOW + " < u.valid_time_end " +
            "and u.transaction_time_end = :infinity " +
            "and d.valid_time_start <= " + NOW +
            "and " + NOW + " < d.valid_time_end " +
            "and d.transaction_time_end = :infinity " +
            "and l.valid_time_start <= " + NOW +
            "and " + NOW + " < l.valid_time_end " +
            "and l.transaction_time_end = :infinity")
    abstract LiveData<FoodItemView> getItem(int id, Instant infinity);

    @Query("select max(i.eat_by) " +
            "from FoodItem i " +
            "where i.of_type = :foodType " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "group by null " +
            "union all " +
            "select max(i.eat_by) " +
            "from FoodItem i " +
            "where i.of_type = :foodType " +
            "and i.transaction_time_end = :infinity " +
            "and i.version = (select max(i2.version) from fooditem i2 where i2._id = i._id) " +
            "group by null " +
            "limit 1")
    abstract LiveData<Instant> getLatestExpirationOf(int foodType, Instant infinity);

    @Query("select count(*) " +
            "from FoodItem i " +
            "where i.of_type = :foodId " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity")
    abstract LiveData<Integer> countItemsOfType(int foodId, Instant infinity);

    @Query("delete from FoodItem")
    abstract void delete();
}
