/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.database;

import androidx.room.Dao;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
abstract class UnitDao {

    @Query("select * " +
            "from current_unit")
    abstract List<UnitDbEntity> getAll();

    @Query("select * " +
            "from current_unit " +
            "order by name")
    abstract Observable<List<UnitForListing>> getCurrentUnits();

    @Query("select * " +
            "from current_unit " +
            "order by name")
    abstract Observable<List<UnitForSelection>> getCurrentUnitsForSelection();

    @Query("select * from current_unit where id = :id")
    abstract UnitForDeletion getUnit(int id);

    @Query("select * from current_unit where id = :id")
    abstract Observable<UnitToEdit> getUnitToEdit(int id);

    @Query("select * from current_unit where id = :id")
    abstract UnitForEditing getUnitForEditing(int id);
}
