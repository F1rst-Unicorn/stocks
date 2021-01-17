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
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;

public class EanNumberForInsertion implements Insertable<EanNumberRecord, EanNumber> {

    private final int identifiesFood;

    private final String eanNumber;

    public EanNumberForInsertion(int identifiesFood, String eanNumber) {
        this.identifiesFood = identifiesFood;
        this.eanNumber = eanNumber;
    }

    public int getIdentifiesFood() {
        return identifiesFood;
    }

    public String getEanNumber() {
        return eanNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EanNumberForInsertion that = (EanNumberForInsertion) o;
        return getIdentifiesFood() == that.getIdentifiesFood() && getEanNumber().equals(that.getEanNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifiesFood(), getEanNumber());
    }

    @Override
    public InsertOnDuplicateStep<EanNumberRecord> insertValue(InsertSetStep<EanNumberRecord> arg, Principals principals) {
        return arg.columns(EAN_NUMBER.NUMBER, EAN_NUMBER.IDENTIFIES, EAN_NUMBER.INITIATES)
                .values(eanNumber, identifiesFood, principals.getDid());

    }
}
