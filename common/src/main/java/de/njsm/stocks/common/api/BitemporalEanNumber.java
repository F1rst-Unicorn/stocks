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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.Objects;

public class BitemporalEanNumber extends BitemporalData implements Bitemporal<EanNumber>, EanNumber {

    private final int identifiesFood;

    private final String eanNumber;

    public BitemporalEanNumber(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, int identifiesFood, String eanNumber) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.identifiesFood = identifiesFood;
        this.eanNumber = eanNumber;
    }

    @Override
    public int getIdentifiesFood() {
        return identifiesFood;
    }

    @Override
    @JsonIgnore
    public String getEanNumber() {
        return eanNumber;
    }

    /**
     * JSON property name. Keep for backward compatibility
     */
    public String getEanCode() {
        return eanNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BitemporalEanNumber that = (BitemporalEanNumber) o;
        return getIdentifiesFood() == that.getIdentifiesFood() && getEanNumber().equals(that.getEanNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdentifiesFood(), getEanNumber());
    }

    @Override
    public boolean isContainedIn(EanNumber item) {
        return Bitemporal.super.isContainedIn(item) &&
                identifiesFood == item.getIdentifiesFood() &&
                eanNumber.equals(item.getEanNumber());
    }
}
