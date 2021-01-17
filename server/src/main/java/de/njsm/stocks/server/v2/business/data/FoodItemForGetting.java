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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.json.InstantSerialiser;

import java.time.Instant;
import java.util.Objects;

public class FoodItemForGetting extends VersionedData implements Versionable<FoodItem>, FoodItem {

    private final Instant eatByDate;

    private final int ofType;

    private final int storedIn;

    private final int registers;

    private final int buys;

    public FoodItemForGetting(int id, int version, Instant eatByDate, int ofType, int storedIn, int registers, int buys) {
        super(id, version);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant getEatByDate() {
        return eatByDate;
    }

    public int getOfType() {
        return ofType;
    }

    public int getStoredIn() {
        return storedIn;
    }

    public int getRegisters() {
        return registers;
    }

    public int getBuys() {
        return buys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodItemForGetting that = (FoodItemForGetting) o;
        return getOfType() == that.getOfType() && getStoredIn() == that.getStoredIn() && getRegisters() == that.getRegisters() && getBuys() == that.getBuys() && getEatByDate().equals(that.getEatByDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEatByDate(), getOfType(), getStoredIn(), getRegisters(), getBuys());
    }
}
