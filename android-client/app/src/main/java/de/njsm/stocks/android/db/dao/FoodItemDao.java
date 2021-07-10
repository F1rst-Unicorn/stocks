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
import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.db.views.ScaledAmount;
import org.threeten.bp.Instant;

import java.util.List;

import static de.njsm.stocks.android.db.entities.Sql.*;
import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
@RewriteQueriesToDropUnusedColumns
public abstract class FoodItemDao implements Inserter<FoodItem> {

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

    public LiveData<List<ScaledAmount>> countItemsOfType(int foodId) {
        return countItemsOfType(foodId, DATABASE_INFINITY);
    }

    public LiveData<List<FoodItem>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    @Query("select * " +
            "from fooditem " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<FoodItem>> getAll(Instant infinity);

    @Query("select " +
            Sql.USER_FIELDS_QUALIFIED +
            Sql.USER_DEVICE_FIELDS_QUALIFIED +
            Sql.LOCATION_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.FOODITEM_FIELDS +
            "1 from fooditem fooditem " +
            Sql.USER_JOIN_FOODITEM +
            Sql.USER_DEVICE_JOIN_FOODITEM +
            Sql.LOCATION_JOIN_FOODITEM +
            Sql.SCALED_UNIT_JOIN_FOODITEM +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "where fooditem.of_type = :foodId " +
            "and fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_end = :infinity " +
            "order by fooditem.eat_by")
    abstract LiveData<List<FoodItemView>> getItemsOfType(int foodId, Instant infinity);

    @Query("select " +
            Sql.USER_FIELDS_QUALIFIED +
            Sql.USER_DEVICE_FIELDS_QUALIFIED +
            Sql.LOCATION_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.FOODITEM_FIELDS +
            "1 from fooditem fooditem " +
            Sql.USER_JOIN_FOODITEM +
            Sql.USER_DEVICE_JOIN_FOODITEM +
            Sql.LOCATION_JOIN_FOODITEM +
            Sql.SCALED_UNIT_JOIN_FOODITEM +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "where fooditem._id = :id " +
            "and fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_end = :infinity")
    abstract LiveData<FoodItemView> getItem(int id, Instant infinity);

    @Query("select max(i.eat_by) " +
            "from fooditem i " +
            "where i.of_type = :foodType " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "group by null " +
            "union all " +
            "select max(i.eat_by) " +
            "from fooditem i " +
            "where i.of_type = :foodType " +
            "and i.transaction_time_end = :infinity " +
            "and i.version = (select max(i2.version) from fooditem i2 where i2._id = i._id) " +
            "group by null " +
            "limit 1")
    abstract LiveData<Instant> getLatestExpirationOf(int foodType, Instant infinity);

    @Query("select " +
            SCALED_UNIT_FIELDS_QUALIFIED +
            UNIT_FIELDS_QUALIFIED +
            "count(*) as amount " +
            "from fooditem fooditem " +
            SCALED_UNIT_JOIN_FOODITEM +
            UNIT_JOIN_SCALED_UNIT +
            "where fooditem.of_type = :foodId " +
            "and fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_end = :infinity " +
            "group by scaled_unit._id " +
            "order by unit._id")
    abstract LiveData<List<ScaledAmount>> countItemsOfType(int foodId, Instant infinity);

    @Query("delete from fooditem")
    abstract void delete();

    @Query("select " +
            Sql.USER_FIELDS_QUALIFIED +
            Sql.USER_DEVICE_FIELDS_QUALIFIED +
            Sql.LOCATION_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.FOODITEM_FIELDS +
            "1 from fooditem fooditem " +
            Sql.USER_JOIN_FOODITEM_AT_TRANSACTION_TIME +
            Sql.USER_DEVICE_JOIN_FOODITEM_AT_TRANSACTION_TIME +
            Sql.LOCATION_JOIN_FOODITEM_AT_TRANSACTION_TIME +
            Sql.SCALED_UNIT_JOIN_FOODITEM_AT_TRANSACTION_TIME +
            Sql.UNIT_JOIN_SCALED_UNIT_AT_TRANSACTION_TIME +
            "where fooditem._id = :id " +
            "and fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_start <= :transactionTime " +
            "and :transactionTime < fooditem.transaction_time_end")
    public abstract LiveData<FoodItemView> getNowAsKnownBy(int id, Instant transactionTime);
}
