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

package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.math.BigDecimal;
import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.SCALED_UNIT;

public class ScaledUnitForInsertion implements Insertable<ScaledUnitRecord, ScaledUnit> {

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
    public InsertOnDuplicateStep<ScaledUnitRecord> insertValue(InsertSetStep<ScaledUnitRecord> insertInto, Principals principals) {
        return insertInto.columns(SCALED_UNIT.SCALE, SCALED_UNIT.UNIT, SCALED_UNIT.INITIATES)
                .values(scale, unit, principals.getDid());
    }

    @Override
    public boolean isContainedIn(ScaledUnit entity) {
        return scale.equals(entity.getScale()) &&
                unit == entity.getUnit();
    }
}
