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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.StreamSupport;

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

    public BigDecimal getTotalAmount() {
        return scaledUnit.getScale().multiply(BigDecimal.valueOf(amount));
    }

    public String getPrettyString() {
        ScaledUnit copy = getScaledUnit().copy();
        copy.setScale(copy.getScale().multiply(new BigDecimal(getAmount())));
        return ScaledUnitView.getPrettyName(copy, getUnit());
    }

    public static String getPrettyString(List<ScaledAmount> amounts) {
        StringJoiner joiner = new StringJoiner(", ");
        StreamSupport.stream(new Aggregator(amounts.iterator()), false)
                .map(ScaledAmount::getPrettyString)
                .forEach(joiner::add);
        return joiner.toString();
    }

    private static class Aggregator extends de.njsm.stocks.android.db.util.SingleTypeAggregator<ScaledAmount> {

        public Aggregator(Iterator<ScaledAmount> iterator) {
            super(iterator);
        }

        @Override
        public boolean sameGroup(ScaledAmount current, ScaledAmount input) {
            return current.getUnit().getId() == input.getUnit().getId();
        }

        @Override
        public ScaledAmount merge(ScaledAmount current, ScaledAmount input) {
            BigDecimal total = input.getScaledUnit().getScale().multiply(new BigDecimal(input.getAmount()))
                    .add(current.getScaledUnit().getScale().multiply(new BigDecimal(current.getAmount())));
            ScaledUnit newScaledUnit = current.scaledUnit.copy();
            newScaledUnit.setScale(total);
            return new ScaledAmount(1, newScaledUnit, current.getUnit());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScaledAmount)) return false;
        ScaledAmount that = (ScaledAmount) o;
        return getAmount() == that.getAmount() && getScaledUnit().equals(that.getScaledUnit()) && getUnit().equals(that.getUnit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getScaledUnit(), getUnit());
    }
}
