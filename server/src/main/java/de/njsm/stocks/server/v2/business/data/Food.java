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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.server.v2.business.data.visitor.AbstractVisitor;
import de.njsm.stocks.server.v2.business.json.PeriodDeserialiser;
import de.njsm.stocks.server.v2.business.json.PeriodSerialiser;

import java.time.Instant;
import java.time.Instant;
import java.time.Period;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Food extends VersionedData {

    public String name;

    public boolean toBuy;

    @JsonSerialize(using = PeriodSerialiser.class)
    @JsonDeserialize(using = PeriodDeserialiser.class)
    public Period expirationOffset;

    public Integer location;

    public Food(int id, int version) {
        super(id, version);
    }

    public Food(int id, String name, int version, boolean toBuy, Period expirationOffset, Integer location) {
        super(id, version);
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
    }

    public Food(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, String name, boolean toBuy, Period expirationOffset, Integer location) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd);
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
    }

    public Food(String name) {
        this.name = name;
        expirationOffset = Period.ZERO;
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.food(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (toBuy != food.toBuy) return false;
        if (!name.equals(food.name)) return false;
        if (location != food.location) return false;
        return expirationOffset.equals(food.expirationOffset);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (toBuy ? 1 : 0);
        result = 31 * result + expirationOffset.hashCode();
        result = 31 * result + location;
        return result;
    }

    @Override
    public String toString() {
        return "Food (" + id + ", " + name + ", " + version + ", " + toBuy + ", " + location + ")";
    }
}
