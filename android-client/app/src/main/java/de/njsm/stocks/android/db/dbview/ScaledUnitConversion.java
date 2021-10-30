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

import static de.njsm.stocks.android.db.dbview.ScaledUnitConversion.QUERY;

@DatabaseView(viewName = ScaledUnitConversion.SCALED_UNIT_CONVERSION_TABLE, value = QUERY)
public class ScaledUnitConversion {

    public static final String SCALED_UNIT_CONVERSION_TABLE = "current_scaled_unit_conversion";

    public static final String SCALED_UNIT_CONVERSION_PREFIX = SCALED_UNIT_CONVERSION_TABLE + "_";

    public static final String QUERY = "select source._id as source_id, target._id as target_id, " +
            "cast(source.scale as numeric) / cast(target.scale as numeric) as factor " +
            "from current_scaled_unit source " +
            "join current_scaled_unit target on source.unit = target.unit";

    public static final String SCALED_AMOUNT_FIELDS_QUALIFIED =
            SCALED_UNIT_CONVERSION_TABLE + ".source_id as " + SCALED_UNIT_CONVERSION_PREFIX + "source_id, " +
            SCALED_UNIT_CONVERSION_TABLE + ".target_id as " + SCALED_UNIT_CONVERSION_PREFIX + "target_id, " +
            SCALED_UNIT_CONVERSION_TABLE + ".factor as " + SCALED_UNIT_CONVERSION_PREFIX + "factor, ";

    @ColumnInfo(name = "source_id")
    private final int sourceId;

    @ColumnInfo(name = "target_id")
    private final int targetId;

    @ColumnInfo(name = "factor")
    private final double factor;

    public ScaledUnitConversion(int sourceId, int targetId, double factor) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.factor = factor;
    }
}
