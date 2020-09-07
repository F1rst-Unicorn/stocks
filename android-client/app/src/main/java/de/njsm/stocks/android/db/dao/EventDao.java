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

import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;
import androidx.room.Dao;
import androidx.room.Query;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.db.views.EventHistoryView;

import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class EventDao {

    public DataSource.Factory<Integer, EntityEvent<?>> getEventHistory() {
        return getEventHistory(DATABASE_INFINITY)
                .map(EventHistoryView::mapToEvent);
    }

    private static final String TIME_COLUMNS =
            "l1._id as version1__id, l1.version as version1_version, l1.valid_time_start as version1_valid_time_start, l1.valid_time_end as version1_valid_time_end, l1.transaction_time_start as version1_transaction_time_start, l1.transaction_time_end as version1_transaction_time_end, " +
            "l2._id as version2__id, l2.version as version2_version, l2.valid_time_start as version2_valid_time_start, l2.valid_time_end as version2_valid_time_end, l2.transaction_time_start as version2_transaction_time_start, l2.transaction_time_end as version2_transaction_time_end ";

    private static final String ON_CHRONOLOGY =
            "on l1.transaction_time_start = l2.transaction_time_start and l1.version + 1 = l2.version ";

    private static final String WHERE_VALID =
            "where not (l1.version != 0 and l2._id is null and l1.transaction_time_end != :infinity) ";

    @Query("select " +
            "'location' as entity, " +
            "'' as food1_name, 0 as food1_to_buy, 0 as food1_expiration_offset, 0 as food1_location, l1.name as location1_name, '' as eannumber1_number, " +
            "'' as food2_name, 0 as food2_to_buy, 0 as food2_expiration_offset, 0 as food2_location, l2.name as location2_name, '' as eannumber2_number, " +
            TIME_COLUMNS +
            "from location l1 " +
            "left outer join location l2 " + ON_CHRONOLOGY +
            WHERE_VALID +

            "union select " +
            "'eannumber' as entity, " +
            "'' as food1_name, 0 as food1_to_buy, 0 as food1_expiration_offset, 0 as food1_location, '' as location1_name, l1.number as eannumber1_number, " +
            "'' as food2_name, 0 as food2_to_buy, 0 as food2_expiration_offset, 0 as food2_location, '' as location2_name, l2.number as eannumber2_number, " +
            TIME_COLUMNS +
            "from eannumber l1 " +
            "left outer join eannumber l2 " + ON_CHRONOLOGY +
            WHERE_VALID +

            "union select " +
            "'food' as entity, " +
            "l1.name as food1_name, l1.to_buy as food1_to_buy, l1.expiration_offset as food1_expiration_offset, l1.location as food1_location, '' as location1_name, '' as eannumber1_number, " +
            "l2.name as food2_name, l2.to_buy as food2_to_buy, l2.expiration_offset as food2_expiration_offset, l2.location as food2_location, '' as location2_name, '' as eannumber2_number, " +
            TIME_COLUMNS +
            "from food l1 " +
            "left outer join food l2 " + ON_CHRONOLOGY +
            WHERE_VALID +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, EventHistoryView> getEventHistory(Instant infinity);
}
