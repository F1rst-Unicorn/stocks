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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Unit;
import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;

@Dao
abstract class PlotDao {

    @Query("with change_of_unit as (" +
                "select " +
                    "f1.valid_time_end as valid_time, " +
                    "f1.transaction_time_start as event_time, " +
                    "f1.unit as old_unit, " +
                    "f2.unit as new_unit " +
                "from food_item f1, food_item f2 " +
                "where f1.id = f2.id " +
                "and f1.of_type = :foodId " +
                "and f1.valid_time_end = f2.valid_time_start " +
                "and f1.unit != f2.unit " +
                "and f1.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                "and f2.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ") " +
            // item added
            "select main_table.valid_time_start as x, " +
                "scaled_unit.scale as y, " +
                "unit.id unit, " +
                "unit.abbreviation as abbreviation " +
            "from food_item main_table " +
            "join scaled_unit scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "where main_table.of_type = :foodId " +
            "and main_table.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and main_table.valid_time_start = (" +
                "select min(valid_time_start) " +
                "from food_item i2 " +
                "where i2.id = main_table.id " +
                "and i2.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ") union all " +

            // item deleted
            "select main_table.valid_time_end as x, " +
                "'-' || scaled_unit.scale as y, " +
                "unit.id unit, " +
                "unit.abbreviation as abbreviation " +
            "from food_item main_table " +
            "join scaled_unit scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "where main_table.of_type = :foodId " +
            "and main_table.valid_time_end != " + DATABASE_INFINITY_STRING_SQL +
            "and main_table.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and main_table.valid_time_start = (" +
                "select max(valid_time_start) " +
                "from food_item i2 " +
                "where i2.id = main_table.id " +
                "and i2.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ") union all " +

            // change_of_unit new item increased
            "select main_table.valid_time as x, " +
                "scaled_unit.scale as y, " +
                "unit.id unit, " +
                "unit.abbreviation as abbreviation " +
            "from change_of_unit main_table " +
            "join scaled_unit scaled_unit on scaled_unit.id = main_table.new_unit " +
                "and scaled_unit.valid_time_start <= main_table.event_time " +
                "and main_table.event_time < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.event_time " +
                "and main_table.event_time < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "union all " +

            // change_of_unit old item decreased
            "select main_table.valid_time as x, " +
                "'-' || scaled_unit.scale as y, " +
                "unit.id unit, " +
                "unit.abbreviation as abbreviation " +
            "from change_of_unit main_table " +
            "join scaled_unit scaled_unit on scaled_unit.id = main_table.old_unit " +
                "and scaled_unit.valid_time_start <= main_table.event_time " +
                "and main_table.event_time < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.event_time " +
                "and main_table.event_time < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "order by unit, x")
    abstract Observable<List<AmountOverTimePoint>> getAmountsOverTimeOf(int foodId);

    static final class AmountOverTimePoint {
        Instant x;
        BigDecimal y;
        Id<Unit> unit;
        String abbreviation;
    }

    @Query("select round(julianday(i.eat_by) - julianday(i.valid_time_end) - 0.5) + 1 x, count(*) y " +
            "from food_item i " +
            "where i.of_type = :foodId " +
            "and i.valid_time_end != " + DATABASE_INFINITY_STRING_SQL +
            "and i.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and i.valid_time_start = (" +
                "select max(valid_time_start) " +
                "from food_item i2 " +
                "where i2.id = i.id " +
                "and i2.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ") " +
            "group by x " +
            "order by x")
    abstract Observable<List<RawPlotPoint>> getEatByExpirationHistogram(int foodId);

    static final class RawPlotPoint {
        int x;
        BigDecimal y;
    }
}
