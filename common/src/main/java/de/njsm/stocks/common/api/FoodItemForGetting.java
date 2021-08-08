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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;

import java.time.Instant;
import java.util.Objects;

public class FoodItemForGetting extends VersionedData implements Versionable<FoodItem>, FoodItem {

    private final Instant eatByDate;

    private final int ofType;

    private final int storedIn;

    private final int registers;

    private final int buys;

    private final int unit;

    public FoodItemForGetting(int id, int version, Instant eatByDate, int ofType, int storedIn, int registers, int buys, int unit) {
        super(id, version);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
        this.unit = unit;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant eatByDate() {
        return eatByDate;
    }

    public int ofType() {
        return ofType;
    }

    public int storedIn() {
        return storedIn;
    }

    public int registers() {
        return registers;
    }

    public int buys() {
        return buys;
    }

    public int unit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodItemForGetting that = (FoodItemForGetting) o;
        return ofType() == that.ofType() && storedIn() == that.storedIn() && registers() == that.registers() && buys() == that.buys() && eatByDate().equals(that.eatByDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eatByDate(), ofType(), storedIn(), registers(), buys());
    }

    @Override
    public boolean isContainedIn(FoodItem item, boolean increment) {
        return FoodItem.super.isContainedIn(item, increment) &&
                eatByDate.equals(item.eatByDate()) &&
                ofType == item.ofType() &&
                storedIn == item.storedIn() &&
                registers == item.registers() &&
                buys == item.buys() &&
                unit == item.unit();
    }
}
