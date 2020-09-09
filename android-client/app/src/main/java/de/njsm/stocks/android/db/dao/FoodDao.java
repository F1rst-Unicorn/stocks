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

import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.FoodWithLatestItemView;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Food[] food);

    public LiveData<List<Food>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    public LiveData<Food> getFood(int id) {
        return getFood(id, DATABASE_INFINITY);
    }

    @Transaction
    public void synchronise(Food[] food) {
        delete();
        insert(food);
    }

    public LiveData<List<Food>> getEmptyFood() {
        return getEmptyFood(DATABASE_INFINITY);
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodToEat() {
        return getFoodToEat(DATABASE_INFINITY);
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodByLocation(int location) {
        return getFoodByLocation(location, DATABASE_INFINITY);
    }

    public LiveData<Food> getFoodByEanNumber(String s) {
        return getFoodByEanNumber(s, DATABASE_INFINITY);
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodBySubString(String searchTerm) {
        return getFoodBySubString(searchTerm, DATABASE_INFINITY);
    }

    public LiveData<List<FoodWithLatestItemView>> getFoodToBuy() {
        return getFoodToBuy(DATABASE_INFINITY);
    }

    @Query("select * " +
            "from Food " +
            "where _id = :id " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<Food> getFood(int id, Instant infinity);

    @Query("select * " +
            "from Food " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by name")
    abstract LiveData<List<Food>> getAll(Instant infinity);

    @Query("select * " +
            "from Food f " +
            "where f.valid_time_start <= " + NOW +
            "and " + NOW + " < f.valid_time_end " +
            "and f.transaction_time_end = :infinity " +
            "and _id not in " +
            "(select distinct of_type from FoodItem i " +
            "where i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity) " +
            "order by _id")
    abstract LiveData<List<Food>> getEmptyFood(Instant infinity);

    @Query("with least_item as (" +
                "select i.of_type, count(*) as amount, i.eat_by as eatBy " +
                "from FoodItem i " +
                "where i.valid_time_start <= " + NOW +
                "and " + NOW + " < i.valid_time_end " +
                "and i.transaction_time_end = :infinity " +
                "group by i.of_type " +
                "having i.eat_by = MIN(i.eat_by)) " +
            "select f._id, f.version, f.name as name, f.to_buy as toBuy, i.eatBy as eatBy, " +
            "i.amount as amount, f.expiration_offset as expirationOffset, f.location as location, " +
            "f.valid_time_start, f.valid_time_end, f.transaction_time_start, f.transaction_time_end " +
            "from Food f " +
            "inner join least_item i on i.of_type = f._id " +
            "where f.valid_time_start <= " + NOW +
            "and " + NOW + " < f.valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by eatBy")
    abstract LiveData<List<FoodWithLatestItemView>> getFoodToEat(Instant infinity);

    @Query("with least_item as (" +
            "select i.of_type, count(*) as amount, i.eat_by as eatBy " +
            "from FoodItem i " +
            "where i.stored_in = :location " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + " < i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "group by i.of_type " +
            "having i.eat_by = MIN(i.eat_by)) " +
            "select f._id, f.version, f.name as name, f.to_buy as toBuy, i.eatBy as eatBy, i.amount as amount, f.expiration_offset as expirationOffset, f.location as location, f.valid_time_start, f.valid_time_end, f.transaction_time_start, f.transaction_time_end " +
            "from Food f " +
            "inner join least_item i on i.of_type = f._id " +
            "and f.valid_time_start <= " + NOW +
            "and " + NOW + " < f.valid_time_end " +
            "and f.transaction_time_end = :infinity " +
            "order by eatBy")
    abstract LiveData<List<FoodWithLatestItemView>> getFoodByLocation(int location, Instant infinity);

    @Query("select f._id, f.version, f.name, f.to_buy, f.expiration_offset, f.location as location, f.valid_time_start, f.valid_time_end, f.transaction_time_start, f.transaction_time_end " +
            "from Food f " +
            "inner join EanNumber n on n.identifies = f._id " +
            "where n.number = :s " +
            "and f.valid_time_start <= " + NOW +
            "and " + NOW + " < f.valid_time_end " +
            "and f.transaction_time_end = :infinity " +
            "limit 1")
    abstract LiveData<Food> getFoodByEanNumber(String s, Instant infinity);

    @Query(     "select f._id as _id, f.version as version, f.name as name, f.to_buy as toBuy, f.expiration_offset as expirationOffset, f.location as location, count(*) as amount, f.valid_time_start as valid_time_start, f.valid_time_end as valid_time_end, f.transaction_time_start as transaction_time_start, f.transaction_time_end as transaction_time_end " +
                "from Food f " +
                "inner join FoodItem i on f._id = i.of_type " +
                "where f.name like :searchTerm " +
                "and f.valid_time_start <= " + NOW +
                "and " + NOW + " < f.valid_time_end " +
                "and f.transaction_time_end = :infinity " +
                "group by f.name " +
            "union " +
                "select f._id as _id, f.version as version, f.name as name, f.to_buy as toBuy, f.expiration_offset as expirationOffset, f.location as location, 0 as amount, f.valid_time_start as valid_time_start, f.valid_time_end as valid_time_end, f.transaction_time_start as transaction_time_start, f.transaction_time_end as transaction_time_end " +
                "from Food f " +
                "where f.name like :searchTerm " +
                "and f.valid_time_start <= " + NOW +
                "and " + NOW + " < f.valid_time_end " +
                "and f.transaction_time_end = :infinity " +
                "and f._id not in (" +
                    "select distinct i.of_type " +
                    "from FoodItem i " +
                    "where i.valid_time_start <= " + NOW +
                    "and " + NOW + " < i.valid_time_end " +
                    "and i.transaction_time_end = :infinity) " +
            "order by name")
    abstract LiveData<List<FoodWithLatestItemView>> getFoodBySubString(String searchTerm, Instant infinity);

    @Query(     "select f._id as _id, f.version as version, f.name as name, f.to_buy as toBuy, f.expiration_offset as expirationOffset, f.location as location, count(*) as amount, f.valid_time_start as valid_time_start, f.valid_time_end as valid_time_end, f.transaction_time_start as transaction_time_start, f.transaction_time_end as transaction_time_end " +
                "from Food f " +
                "inner join FoodItem i on f._id = i.of_type " +
                "where f.to_buy " +
                "and f.valid_time_start <= " + NOW +
                "and " + NOW + " < f.valid_time_end " +
                "and f.transaction_time_end = :infinity " +
                "group by f.name " +
            "union " +
                "select f._id as _id, f.version as version, f.name as name, f.to_buy as toBuy, f.expiration_offset as expirationOffset, f.location as location, 0 as amount, f.valid_time_start as valid_time_start, f.valid_time_end as valid_time_end, f.transaction_time_start as transaction_time_start, f.transaction_time_end as transaction_time_end " +
                "from Food f " +
                "where f.to_buy " +
                "and f.valid_time_start <= " + NOW +
                "and " + NOW + " < f.valid_time_end " +
                "and f.transaction_time_end = :infinity " +
                "and f._id not in (" +
                    "select distinct of_type " +
                    "from FoodItem i " +
                    "where i.valid_time_start <= " + NOW +
                    "and " + NOW + " < i.valid_time_end " +
                    "and i.transaction_time_end = :infinity) " +
            "order by name")
    abstract LiveData<List<FoodWithLatestItemView>> getFoodToBuy(Instant infinity);

    @Query("delete from Food")
    abstract void delete();
}
