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

package de.njsm.stocks.common.api;

import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import java.math.BigDecimal;
import java.util.Objects;

public class ScaledUnitForInsertion implements Insertable<ScaledUnit> {

    private final BigDecimal scale;

    private final int unit;

    public ScaledUnitForInsertion(BigDecimal scale, int unit) {
        this.scale = scale;
        this.unit = unit;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public int getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScaledUnitForInsertion)) return false;
        ScaledUnitForInsertion that = (ScaledUnitForInsertion) o;
        return getUnit() == that.getUnit() && getScale().equals(that.getScale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScale(), getUnit());
    }

    @Override
    public boolean isContainedIn(ScaledUnit entity) {
        return scale.equals(entity.scale()) &&
                unit == entity.unit();
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.scaledUnitForInsertion(this, argument);
    }
}
