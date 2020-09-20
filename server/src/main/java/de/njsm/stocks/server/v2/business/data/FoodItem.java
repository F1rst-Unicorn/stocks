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
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.json.InstantSerialiser;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class FoodItem extends VersionedData {

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant eatByDate;

    public int ofType;

    public int storedIn;

    public int registers;

    public int buys;

    public FoodItem(int id,
                    int version,
                    Instant eatByDate,
                    int ofType,
                    int storedIn,
                    int registers,
                    int buys) {
        super(id, version);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem(Instant eatByDate,
                    int ofType,
                    int storedIn,
                    int registers,
                    int buys) {
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, Instant eatByDate, int ofType, int storedIn, int registers, int buys, int creatorUser, int creatorUserDevice) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, creatorUser, creatorUserDevice);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem(int id, int version) {
        super(id, version);
        eatByDate = Instant.EPOCH;
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I input) {
        return visitor.foodItem(this, input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodItem foodItem = (FoodItem) o;

        if (id != foodItem.id) return false;
        if (ofType != foodItem.ofType) return false;
        if (storedIn != foodItem.storedIn) return false;
        if (registers != foodItem.registers) return false;
        if (buys != foodItem.buys) return false;
        return eatByDate.equals(foodItem.eatByDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eatByDate, ofType, storedIn, registers, buys);
    }

    @Override
    public String toString() {
        return "FoodItem (" + id + ", " + version + ", " + eatByDate + ", " + ofType + ", " + storedIn + ", " + registers + ", " + buys + ")";
    }
}
