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

import java.util.Objects;

public class EanNumberForGetting extends VersionedData implements Versionable<EanNumber>, EanNumber {

    private final int identifiesFood;

    private final String eanNumber;

    public EanNumberForGetting(int id, int version, int identifiesFood, String eanNumber) {
        super(id, version);
        this.identifiesFood = identifiesFood;
        this.eanNumber = eanNumber;
    }

    public int getIdentifiesFood() {
        return identifiesFood;
    }

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
        EanNumberForGetting that = (EanNumberForGetting) o;
        return getIdentifiesFood() == that.getIdentifiesFood() && getEanNumber().equals(that.getEanNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdentifiesFood(), getEanNumber());
    }

    @Override
    public boolean isContainedIn(EanNumber item) {
        return EanNumber.super.isContainedIn(item) &&
                identifiesFood == item.getIdentifiesFood() &&
                eanNumber.equals(item.getEanNumber());
    }
}
