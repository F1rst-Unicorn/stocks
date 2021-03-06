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

import java.time.Period;
import java.util.Objects;

public class FoodForGetting extends VersionedData implements Food {

    private final String name;

    private final boolean toBuy;

    @JsonSerialize(using = PeriodSerialiser.class)
    @JsonDeserialize(using = PeriodDeserialiser.class)
    private final Period expirationOffset;

    private final Integer location;

    private final String description;

    public FoodForGetting(int id, int version, String name, boolean toBuy, Period expirationOffset, Integer location, String description) {
        super(id, version);
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public boolean isToBuy() {
        return toBuy;
    }

    public Period getExpirationOffset() {
        return expirationOffset;
    }

    public Integer getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodForGetting that = (FoodForGetting) o;
        return isToBuy() == that.isToBuy() && getName().equals(that.getName()) && getExpirationOffset().equals(that.getExpirationOffset()) && getLocation().equals(that.getLocation()) && getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), isToBuy(), getExpirationOffset(), getLocation(), getDescription());
    }
}
