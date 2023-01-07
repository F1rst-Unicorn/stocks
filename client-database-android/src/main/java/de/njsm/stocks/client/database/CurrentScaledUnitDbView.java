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

package de.njsm.stocks.client.database;

import androidx.room.DatabaseView;
import com.google.auto.value.AutoValue;

import java.math.BigDecimal;
import java.time.Instant;

import static de.njsm.stocks.client.database.CurrentTable.NOW_AS_BEST_KNOWN;

@DatabaseView(viewName = CurrentScaledUnitDbView.CURRENT_SCALED_UNIT_TABLE, value =
        "select * " +
        "from scaled_unit " +
        NOW_AS_BEST_KNOWN)
@AutoValue
abstract class CurrentScaledUnitDbView implements IdFields, BitemporalFields, ScaledUnitFields {

    static final String CURRENT_SCALED_UNIT_TABLE = "current_scaled_unit";

    static CurrentScaledUnitDbView create(int id,
                                          int version,
                                          Instant validTimeStart,
                                          Instant validTimeEnd,
                                          Instant transactionTimeStart,
                                          Instant transactionTimeEnd,
                                          int initiates,
                                          BigDecimal scale,
                                          int unit) {
        return new AutoValue_CurrentScaledUnitDbView(
                id,
                version,
                validTimeStart,
                validTimeEnd,
                transactionTimeStart,
                transactionTimeEnd,
                initiates,
                scale,
                unit
        );
    }
}
