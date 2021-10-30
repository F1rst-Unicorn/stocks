/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.db.dbview;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.entities.Unit;

import static de.njsm.stocks.android.db.dbview.ScaledAmount.QUERY;
import static de.njsm.stocks.android.db.entities.Sql.SCALED_UNIT_PREFIX;
import static de.njsm.stocks.android.db.entities.Sql.UNIT_PREFIX;

@DatabaseView(viewName = ScaledAmount.SCALED_AMOUNT_TABLE, value = QUERY)
public class ScaledAmount {

    public static final String SCALED_AMOUNT_TABLE = "current_scaled_amount";

    public static final String SCALED_AMOUNT_PREFIX = SCALED_AMOUNT_TABLE + "_";

    public static final String QUERY =
            "select " +
                    Sql.UNIT_FIELDS_QUALIFIED +
                    Sql.SCALED_UNIT_FIELDS_QUALIFIED +
                    "fooditem.of_type as of_type, " +
                    "fooditem.stored_in as stored_in, " +
                    "count(*) as amount " +
                    "from current_fooditem fooditem " +
                    Sql.SCALED_UNIT_JOIN_FOODITEM +
                    Sql.UNIT_JOIN_SCALED_UNIT +
                    "group by fooditem.of_type, fooditem.unit " +
            "union all " +
                    "select " +
                    Sql.UNIT_FIELDS_QUALIFIED +
                    Sql.SCALED_UNIT_FIELDS_QUALIFIED +
                    "food._id as of_type, " +
                    "food.location as stored_in, " +
                    "0 as amount " +
                    "from current_food food " +
                    Sql.SCALED_UNIT_JOIN_FOOD +
                    Sql.UNIT_JOIN_SCALED_UNIT +
                    "where food._id not in (" +
                            "select distinct of_type " +
                            "from current_fooditem fooditem)";

    public static final String SCALED_AMOUNT_FIELDS_QUALIFIED =
            SCALED_AMOUNT_TABLE + ".amount as " + SCALED_AMOUNT_PREFIX + "amount, " +
            SCALED_AMOUNT_TABLE + ".of_type as " + SCALED_AMOUNT_PREFIX + "of_type, " +
            SCALED_AMOUNT_TABLE + ".stored_in as " + SCALED_AMOUNT_PREFIX + "stored_in, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "_id as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "_id, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "version as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "version, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "valid_time_start as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "valid_time_start, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "valid_time_end as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "valid_time_end, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "transaction_time_start as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "transaction_time_start, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "transaction_time_end as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "transaction_time_end, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "initiates as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "initiates, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "name as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "name, " +
            SCALED_AMOUNT_TABLE + "." + UNIT_PREFIX + "abbreviation as " + SCALED_AMOUNT_PREFIX + UNIT_PREFIX + "abbreviation, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "_id as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "_id, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "version as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "version, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "valid_time_start as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "valid_time_start, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "valid_time_end as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "valid_time_end, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "transaction_time_start as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "transaction_time_start, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "transaction_time_end as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "transaction_time_end, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "initiates as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "initiates, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "scale as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "scale, " +
            SCALED_AMOUNT_TABLE + "." + SCALED_UNIT_PREFIX + "unit as " + SCALED_AMOUNT_PREFIX + SCALED_UNIT_PREFIX + "unit, ";

    private final int amount;

    @ColumnInfo(name = "of_type")
    private final int ofType;

    @ColumnInfo(name = "stored_in")
    private final int storedIn;

    @Embedded(prefix = SCALED_UNIT_PREFIX)
    private final ScaledUnit scaledUnit;

    @Embedded(prefix = UNIT_PREFIX)
    private final Unit unit;

    public ScaledAmount(int amount, int ofType, int storedIn, ScaledUnit scaledUnit, Unit unit) {
        this.amount = amount;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.scaledUnit = scaledUnit;
        this.unit = unit;
    }
}
