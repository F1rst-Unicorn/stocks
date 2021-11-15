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
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.entities.Sql;
import java.time.Instant;

import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY_STRING;

@Dao
public abstract class LocationDao implements Inserter<Location> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Location> locations);

    public LiveData<List<Location>> getAll() {
        return getAll(DATABASE_INFINITY);
    }

    public LiveData<Location> getLocation(int locationId) {
        return getLocation(locationId, DATABASE_INFINITY);
    }

    @Transaction
    public void synchronise(List<Location> locations) {
        delete();
        insert(locations);
    }

    @Query("select * " +
            "from location " +
            "where _id = :locationId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + "< valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<Location> getLocation(int locationId, Instant infinity);

    private static final String LOCATION_WITH_MOST_ITEMS_OF_TYPE = "select " +
            Sql.LOCATION_FIELDS +
            "count(*) as amount " +
            "from location location " +
            Sql.FOODITEM_JOIN_LOCATION +
            "where fooditem.of_type = :food " +
            "and location.valid_time_start <= " + NOW +
            "and " + NOW + "< location.valid_time_end " +
            "and location.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "group by location._id " +
            "order by amount desc " +
            "limit 1";
    @Query(LOCATION_WITH_MOST_ITEMS_OF_TYPE)
    public abstract LiveData<Location> getLocationWithMostItemsOfType(int food);

    @Query(LOCATION_WITH_MOST_ITEMS_OF_TYPE)
    public abstract Location loadLocationWithMostItemsOfType(int food);

    @Query("select * " +
            "from location " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by name")
    abstract LiveData<List<Location>> getAll(Instant infinity);

    @Query("delete from Location")
    abstract void delete();
}
