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
import de.njsm.stocks.android.db.views.AbstractHistoryView;
import de.njsm.stocks.android.db.views.EanNumberHistoryView;
import de.njsm.stocks.android.db.views.FoodHistoryView;
import de.njsm.stocks.android.db.views.LocationHistoryView;
import de.njsm.stocks.android.db.views.UserHistoryView;

import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class EventDao {

    private static final String TIME_COLUMNS =
            "l1._id as version1__id, l1.version as version1_version, l1.valid_time_start as version1_valid_time_start, l1.valid_time_end as version1_valid_time_end, l1.transaction_time_start as version1_transaction_time_start, l1.transaction_time_end as version1_transaction_time_end, " +
            "l2._id as version2__id, l2.version as version2_version, l2.valid_time_start as version2_valid_time_start, l2.valid_time_end as version2_valid_time_end, l2.transaction_time_start as version2_transaction_time_start, l2.transaction_time_end as version2_transaction_time_end ";

    private static final String ON_CHRONOLOGY =
            "on l1.transaction_time_start = l2.transaction_time_start and l1.version + 1 = l2.version ";

    private static final String WHERE_VALID =
            "where not (l1.version != 0 and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = 0)";

    public DataSource.Factory<Integer, EntityEvent<?>> getLocationHistory() {
        return getLocationHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getEanHistory() {
        return getEanHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodHistory() {
        return getFoodHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getUserHistory() {
        return getUserHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    @Query("select " +
            "l1.name as version1_name, " +
            "l2.name as version2_name, " +
            TIME_COLUMNS +
            "from location l1 " +
            "left outer join location l2 " + ON_CHRONOLOGY +
            WHERE_VALID +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, LocationHistoryView> getLocationHistory(Instant infinity);

    @Query("select " +
            "f1.name as version1_identified_food_name, l1.number as version1_number, l1.identifies as version1_identifies," +
            "f2.name as version2_identified_food_name, l2.number as version2_number, l2.identifies as version2_identifies," +
            TIME_COLUMNS +
            "from eannumber l1 " +
            "left outer join eannumber l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.identifies = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.identifies = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, EanNumberHistoryView> getEanHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, l1.location as version1_location, l1.to_buy as version1_to_buy, l1.expiration_offset as version1_expiration_offset, l1.location as version1_location, f1.name as version1_location_name, " +
            "l2.name as version2_name, l2.location as version2_location, l2.to_buy as version2_to_buy, l2.expiration_offset as version2_expiration_offset, l2.location as version2_location, f2.name as version2_location_name, " +
            TIME_COLUMNS +
            "from food l1 " +
            "left outer join food l2 " + ON_CHRONOLOGY +
            "left outer join location f1 on l1.location = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join location f2 on l2.location = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodHistoryView> getFoodHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, " +
            "l2.name as version2_name, " +
            TIME_COLUMNS +
            "from user l1 " +
            "left outer join user l2 " + ON_CHRONOLOGY +
            WHERE_VALID +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, UserHistoryView> getUserHistory(Instant infinity);

}
