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
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Instant;

import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;

@Dao
public abstract class ScaledUnitDao implements Inserter<ScaledUnit> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ScaledUnit[] data);

    @Transaction
    public void synchronise(ScaledUnit[] data) {
        delete();
        insert(data);
    }

    @Query("delete from scaled_unit")
    abstract void delete();

    public LiveData<List<ScaledUnit>> getAll() {
        return getAll(Config.DATABASE_INFINITY);
    }

    @Query("select * " +
            "from scaled_unit " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<ScaledUnit>> getAll(Instant infinity);
}