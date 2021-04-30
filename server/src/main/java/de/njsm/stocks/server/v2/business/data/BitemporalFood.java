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
import de.njsm.stocks.server.v2.business.json.PeriodDeserialiser;
import de.njsm.stocks.server.v2.business.json.PeriodSerialiser;

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
    public String getName() {
        return name;
    }

    @Override
    public boolean isToBuy() {
        return toBuy;
    }

    @JsonSerialize(using = PeriodSerialiser.class)
    @JsonDeserialize(using = PeriodDeserialiser.class)
    @Override
    public Period getExpirationOffset() {
        return expirationOffset;
    }

    @Override
    public Integer getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getStoreUnit() {
        return storeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitemporalFood)) return false;
        if (!super.equals(o)) return false;
        BitemporalFood that = (BitemporalFood) o;
        return isToBuy() == that.isToBuy() && getStoreUnit() == that.getStoreUnit() && getName().equals(that.getName()) && getExpirationOffset().equals(that.getExpirationOffset()) && Objects.equals(getLocation(), that.getLocation()) && getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), isToBuy(), getExpirationOffset(), getLocation(), getDescription(), getStoreUnit());
    }

    @Override
    public boolean isContainedIn(Food item) {
        return Bitemporal.super.isContainedIn(item) &&
                name.equals(item.getName()) &&
                toBuy == item.isToBuy() &&
                expirationOffset.equals(item.getExpirationOffset()) &&
                (location == null) ? location == item.getLocation() : location.equals(item.getLocation()) &&
                description.equals(item.getDescription()) &&
                storeUnit == item.getStoreUnit();
    }
}
