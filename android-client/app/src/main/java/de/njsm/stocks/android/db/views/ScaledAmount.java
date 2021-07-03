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

package de.njsm.stocks.android.db.views;

import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.entities.Unit;

import java.math.BigDecimal;

public class ScaledAmount {

    private final int amount;

    @Embedded(prefix = Sql.SCALED_UNIT_PREFIX)
    private final ScaledUnit scaledUnit;

    @Embedded(prefix = Sql.UNIT_PREFIX)
    private final Unit unit;

    public ScaledAmount(int amount, ScaledUnit scaledUnit, Unit unit) {
        this.amount = amount;
        this.scaledUnit = scaledUnit;
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public ScaledUnit getScaledUnit() {
        return scaledUnit;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getPrettyString() {
        ScaledUnit copy = getScaledUnit().copy();
        copy.setScale(copy.getScale().multiply(new BigDecimal(getAmount())));
        return ScaledUnitView.getPrettyName(copy, getUnit());
    }
}
