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
import androidx.room.Query;
import androidx.room.Transaction;
import de.njsm.stocks.android.db.entities.Location;

import java.util.List;

@Dao
public abstract class LocationDao {

    @Query("SELECT * FROM Location")
    public abstract LiveData<List<Location>> getAll();

    @Query("SELECT * FROM Location WHERE _id = :locationId")
    public abstract LiveData<Location> getLocation(int locationId);

    @Transaction
    public void synchronise(Location[] locations) {
        delete();
        insert(locations);
    }

    @Insert
    abstract void insert(Location[] locations);

    @Query("DELETE FROM Location")
    abstract void delete();

    @Query("SELECT l._id, l.version, l.name, count(*) as amount FROM Location l " +
            "INNER JOIN  FoodItem i on i.stored_in = l._id " +
            "WHERE i.of_type = :food " +
            "GROUP BY l._id " +
            "ORDER BY amount DESC " +
            "LIMIT 1")
    public abstract LiveData<Location> getLocationWithMostItemsOfType(int food);
}
