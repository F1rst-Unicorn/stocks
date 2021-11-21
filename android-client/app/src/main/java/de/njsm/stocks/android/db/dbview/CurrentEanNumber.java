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

import androidx.annotation.NonNull;
import androidx.room.DatabaseView;
import de.njsm.stocks.android.db.entities.EanNumber;

import java.time.Instant;

import static de.njsm.stocks.android.db.dbview.CurrentTable.NOW_AS_BEST_KNOWN;

@DatabaseView(viewName = CurrentEanNumber.CURRENT_EAN_NUMBER_TABLE, value =
        "select * " +
                "from eannumber " +
                NOW_AS_BEST_KNOWN)
public class CurrentEanNumber extends EanNumber {

    public static final String CURRENT_EAN_NUMBER_TABLE = "current_eannumber";

    public CurrentEanNumber(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String eanCode, int identifiesFood) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, eanCode, identifiesFood);
    }
}