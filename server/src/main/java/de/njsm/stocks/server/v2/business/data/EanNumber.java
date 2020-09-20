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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v2.business.data.visitor.AbstractVisitor;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class EanNumber extends VersionedData {

    public String eanCode;

    public int identifiesFood;

    public EanNumber(int id, int version, String eanCode, int identifiesFood) {
        super(id, version);
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    public EanNumber(String eanCode, int identifiesFood) {
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    public EanNumber(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, String eanCode, int identifiesFood, int initiates) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EanNumber eanNumber = (EanNumber) o;
        return id == eanNumber.id &&
                identifiesFood == eanNumber.identifiesFood &&
                Objects.equals(eanCode, eanNumber.eanCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eanCode, identifiesFood);
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.eanNumber(this, arg);
    }
}
