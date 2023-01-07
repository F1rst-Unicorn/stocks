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
import de.njsm.stocks.client.business.entities.UserForDeletion;
import de.njsm.stocks.client.business.entities.UserForListing;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
abstract class UserDao {

    @Query("select * " +
            "from current_user")
    abstract List<UserDbEntity> getAll();

    @Query("select * " +
            "from current_user " +
            "order by name, id")
    abstract Observable<List<UserForListing>> getUsers();

    @Query("select * " +
            "from current_user " +
            "where id = :id")
    abstract Observable<UserForListing> getUser(int id);

    @Query("select * " +
            "from current_user " +
            "where id = :id")
    abstract UserForDeletion getUserForDeletion(int id);
}
