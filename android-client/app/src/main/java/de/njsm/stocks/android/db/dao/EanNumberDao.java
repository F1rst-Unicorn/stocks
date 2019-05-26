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
import de.njsm.stocks.android.db.entities.EanNumber;

import java.util.List;

@Dao
public abstract class EanNumberDao {

    @Query("SELECT * FROM EanNumber " +
            "WHERE identifies = :foodId")
    public abstract LiveData<List<EanNumber>> getEanNumbersOf(int foodId);

    @Transaction
    public void synchronise(EanNumber[] food) {
        delete();
        insert(food);
    }

    @Insert
    abstract void insert(EanNumber[] food);

    @Query("DELETE FROM EanNumber")
    abstract void delete();
}
