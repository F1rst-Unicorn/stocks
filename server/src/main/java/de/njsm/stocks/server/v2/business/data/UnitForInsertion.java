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
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UNIT;

public class UnitForInsertion implements Insertable<UnitRecord, Unit> {

    private final String name;

    private final String abbreviation;

    public UnitForInsertion(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    @Override
    public InsertOnDuplicateStep<UnitRecord> insertValue(InsertSetStep<UnitRecord> insertInto, Principals principals) {
        return insertInto.columns(UNIT.NAME, UNIT.ABBREVIATION, UNIT.INITIATES)
                .values(name, abbreviation, principals.getDid());
    }

    @Override
    public boolean isContainedIn(Unit entity) {
        return name.equals(entity.getName()) &&
                abbreviation.equals(entity.getAbbreviation());
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitForInsertion)) return false;
        UnitForInsertion that = (UnitForInsertion) o;
        return getName().equals(that.getName()) && getAbbreviation().equals(that.getAbbreviation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAbbreviation());
    }
}
