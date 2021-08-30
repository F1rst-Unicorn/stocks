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
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Query;
import com.github.mikephil.charting.data.BarEntry;
import de.njsm.stocks.android.db.views.BarEntryView;
import de.njsm.stocks.android.db.views.PlotPoint;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class PlotDao {

    public LiveData<List<PlotPoint>> getFoodPlot(int foodId) {
        return getFoodPlot(foodId, DATABASE_INFINITY);
    }

    public LiveData<List<BarEntry>> getExpirationHistogram(int foodId) {
        return Transformations.map(getExpirationHistogram(foodId, DATABASE_INFINITY), l ->
                l.stream().map(BarEntryView::map).collect(Collectors.toList()));
    }

    @Query("select valid_time_start as time, 1 as value " +
            "from fooditem i " +
            "where of_type = :foodId " +
            "and transaction_time_end = :infinity " +
            "and version = (select min(version) from fooditem i2 where i2._id = i._id) " +
            "union all " +
            "select valid_time_end as time, -1 as value " +
            "from fooditem i " +
            "where of_type = :foodId " +
            "and transaction_time_end = :infinity " +
            "and valid_time_end != :infinity " +
            "and version = (select max(version) from fooditem i2 where i2._id = i._id) " +
            "order by time")
    abstract LiveData<List<PlotPoint>> getFoodPlot(int foodId, Instant infinity);

    @Query("select round(julianday(i.eat_by) - julianday(i.valid_time_end) + 0.5) as x, count(*) as y " +
            "from fooditem i " +
            "where i.of_type = :foodId " +
            "and i.valid_time_end != :infinity " +
            "and i.transaction_time_end = :infinity " +
            "and i.version = (select max(i2.version) from fooditem i2 where i2._id = i._id) " +
            "group by x " +
            "order by x")
    abstract LiveData<List<BarEntryView>> getExpirationHistogram(int foodId, Instant infinity);
}
