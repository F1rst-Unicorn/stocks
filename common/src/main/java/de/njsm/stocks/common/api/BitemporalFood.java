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
import de.njsm.stocks.common.api.serialisers.PeriodDeserialiser;
import de.njsm.stocks.common.api.serialisers.PeriodSerialiser;

import java.time.Instant;
import java.time.Period;
import java.util.Objects;

public class BitemporalFood extends BitemporalData implements Food, Bitemporal<Food> {

    private final String name;

    private final boolean toBuy;

    private final Period expirationOffset;

    private final Integer location;

    private final String description;

    private final int storeUnit;

    public BitemporalFood(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, String name, boolean toBuy, Period expirationOffset, Integer location, String description, int storeUnit) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.description = description;
        this.storeUnit = storeUnit;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean toBuy() {
        return toBuy;
    }

    @JsonSerialize(using = PeriodSerialiser.class)
    @JsonDeserialize(using = PeriodDeserialiser.class)
    @Override
    public Period expirationOffset() {
        return expirationOffset;
    }

    @Override
    public Integer location() {
        return location;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public int storeUnit() {
        return storeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitemporalFood)) return false;
        if (!super.equals(o)) return false;
        BitemporalFood that = (BitemporalFood) o;
        return toBuy() == that.toBuy() && storeUnit() == that.storeUnit() && name().equals(that.name()) && expirationOffset().equals(that.expirationOffset()) && Objects.equals(location(), that.location()) && description().equals(that.description());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name(), toBuy(), expirationOffset(), location(), description(), storeUnit());
    }

    @Override
    public boolean isContainedIn(Food item) {
        return Bitemporal.super.isContainedIn(item) &&
                name.equals(item.name()) &&
                toBuy == item.toBuy() &&
                expirationOffset.equals(item.expirationOffset()) &&
                (location == null) ? location == item.location() : location.equals(item.location()) &&
                description.equals(item.description()) &&
                storeUnit == item.storeUnit();
    }
}
