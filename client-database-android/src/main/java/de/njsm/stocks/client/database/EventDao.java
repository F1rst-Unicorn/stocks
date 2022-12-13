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
import de.njsm.stocks.client.business.event.LocationEventFeedItem;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;

@Dao
abstract
class EventDao {

    @Query("select max(last_update) " +
            "from updates")
    abstract Observable<Instant> getLatestUpdateTimestamp();

    @Query("select l.valid_time_end as validTimeEnd, " +
                "l.transaction_time_start as transactionTimeStart, " +
                "initiator_user.name as userName, " +
                "l.name as name, " +
                "l.description as description " +
            "from location l " +
            "join user_device initiator_device on l.initiates = initiator_device.id " +
                "and initiator_device.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " " +
            "join user initiator_user on initiator_user.id = initiator_device.belongs_to " +
                "and initiator_user.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " " +
            "where :lower <= l.transaction_time_start " +
            "and l.transaction_time_start <= :upper " +
            "order by l.transaction_time_start desc, l.valid_time_end")
    abstract Single<List<LocationEventFeedItem>> getLocationEvents(Instant lower, Instant upper);

    @Query("select min(x) from (" +
                "select * from (" +
                    "select min(transaction_time_start) x " +
                    "from location" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from unit" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from scaled_unit" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from food" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from food_item" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from ean_number" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from recipe" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from recipe_ingredient" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from recipe_product" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from user" +
                ") union select * from (" +
                    "select min(transaction_time_start) x " +
                    "from user_device" +
                ")" +
            ")")
    abstract Single<Instant> getOldestEventTime();
}
