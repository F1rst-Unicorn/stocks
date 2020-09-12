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

import de.njsm.stocks.android.db.entities.Location;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Location[] locations);

    public LiveData<List<Location>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    public LiveData<Location> getLocationWithMostItemsOfType(int food) {
        return getLocationWithMostItemsOfType(food, DATABASE_INFINITY);
    }

    public LiveData<Location> getLocation(int locationId) {
        return getLocation(locationId, DATABASE_INFINITY);
    }

    @Transaction
    public void synchronise(Location[] locations) {
        delete();
        insert(locations);
    }

    @Query("select * " +
            "from Location " +
            "where _id = :locationId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + "< valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<Location> getLocation(int locationId, Instant infinity);

    @Query("select l._id, l.version, l.name, l.valid_time_start, " +
            "l.valid_time_end, l.transaction_time_start, " +
            "l.transaction_time_end, count(*) as amount " +
            "from Location l " +
            "inner join FoodItem i on i.stored_in = l._id " +
            "where i.of_type = :food " +
            "and l.valid_time_start <= " + NOW +
            "and " + NOW + "< l.valid_time_end " +
            "and l.transaction_time_end = :infinity " +
            "and i.valid_time_start <= " + NOW +
            "and " + NOW + "< i.valid_time_end " +
            "and i.transaction_time_end = :infinity " +
            "group by l._id " +
            "order by amount desc " +
            "limit 1")
    abstract LiveData<Location> getLocationWithMostItemsOfType(int food, Instant infinity);


    @Query("select * " +
            "from Location " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by name")
    abstract LiveData<List<Location>> getAll(Instant infinity);

    @Query("delete from Location")
    abstract void delete();
}
