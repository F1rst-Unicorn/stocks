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
import androidx.room.Insert;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.UserDeviceForDeletion;
import de.njsm.stocks.client.business.entities.UserDeviceForListing;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Dao
abstract class UserDeviceDao {

    @Query("select * " +
            "from current_user_device")
    abstract List<UserDeviceDbEntity> getAll();

    @Query("select d.id as id, d.name as name, ticket.ticket is not null as ticketPresent " +
            "from current_user_device d " +
            "left join ticket on ticket.device_id = d.id " +
            "where d.belongs_to = :id " +
            "order by d.name, d.id")
    abstract Observable<List<UserDeviceForListing>> getUserDevices(int id);

    @Query("select * " +
            "from current_user_device " +
            "where id = :id")
    abstract UserDeviceForDeletion get(int id);

    @Insert
    abstract void store(TicketEntity entity);
}
