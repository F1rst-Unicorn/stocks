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

import de.njsm.stocks.android.db.entities.UserDevice;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class UserDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(UserDevice[] users);

    public LiveData<List<UserDevice>> getDevicesOfUser(int userId) {
        return getCurrentDevicesOfUser(userId, DATABASE_INFINITY);
    }

    @Transaction
    public void synchronise(UserDevice[] users) {
        delete();
        insert(users);
    }

    @Query("select * " +
            "from User_device " +
            "where belongs_to = :userId " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity " +
            "order by name")
    abstract LiveData<List<UserDevice>> getCurrentDevicesOfUser(int userId, Instant infinity);

    @Query("delete from User_device")
    abstract void delete();
}
