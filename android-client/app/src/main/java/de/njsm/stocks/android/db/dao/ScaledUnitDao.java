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
import de.njsm.stocks.android.db.views.ScaledUnitView;
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

    public LiveData<List<ScaledUnitView>> getAllView() {
        return getAllView(Config.DATABASE_INFINITY);
    }

    public LiveData<ScaledUnitView> getScaledUnitView(int id) {
        return getScaledUnitView(id, Config.DATABASE_INFINITY);
    }

    @Query("select su.*, " +
            "u._id as unit__id, u.version as unit_version, u.valid_time_start as unit_valid_time_start, u.valid_time_end as unit_valid_time_end, u.transaction_time_start as unit_transaction_time_start, u.transaction_time_end as unit_transaction_time_end, u.initiates as unit_initiates, u.abbreviation as unit_abbreviation, u.name as unit_name " +
            "from scaled_unit su " +
            "inner join unit u on su.unit = u._id " +
            "where su.valid_time_start <= " + NOW +
            "and " + NOW + " < su.valid_time_end " +
            "and su.transaction_time_end = :infinity " +
            "and u.valid_time_start <= " + NOW +
            "and " + NOW + " < u.valid_time_end " +
            "and u.transaction_time_end = :infinity " +
            "and su._id = :id")
    protected abstract LiveData<ScaledUnitView> getScaledUnitView(int id, Instant infinity);

    @Query("select * " +
            "from scaled_unit " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = :infinity")
    abstract LiveData<List<ScaledUnit>> getAll(Instant infinity);

    @Query("select su.*, " +
            "u._id as unit__id, u.version as unit_version, u.valid_time_start as unit_valid_time_start, u.valid_time_end as unit_valid_time_end, u.transaction_time_start as unit_transaction_time_start, u.transaction_time_end as unit_transaction_time_end, u.initiates as unit_initiates, u.abbreviation as unit_abbreviation, u.name as unit_name " +
            "from scaled_unit su " +
            "inner join unit u on su.unit = u._id " +
            "where su.valid_time_start <= " + NOW +
            "and " + NOW + " < su.valid_time_end " +
            "and su.transaction_time_end = :infinity " +
            "and u.valid_time_start <= " + NOW +
            "and " + NOW + " < u.valid_time_end " +
            "and u.transaction_time_end = :infinity " +
            "order by unit_abbreviation")
    abstract LiveData<List<ScaledUnitView>> getAllView(Instant infinity);
}
