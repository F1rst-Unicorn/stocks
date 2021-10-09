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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;

import java.time.Instant;

public interface FoodItem extends Entity<FoodItem> {

    @JsonGetter
    int registers();

    @JsonGetter
    @JsonSerialize(using = InstantSerialiser.class)
    Instant eatByDate();

    @JsonGetter
    int ofType();

    @JsonGetter
    int storedIn();

    @JsonGetter
    int buys();

    @JsonGetter
    int unit();

    interface Builder<T> {

        T registers(int v);

        @JsonDeserialize(using = InstantDeserialiser.class)
        T eatByDate(Instant v);

        T ofType(int v);

        T storedIn(int v);

        T buys(int v);

        T unit(int v);
    }

    @Override
    default boolean isContainedIn(FoodItem item, boolean increment) {
        return Entity.super.isContainedIn(item, increment) &&
                eatByDate().equals(item.eatByDate()) &&
                ofType() == item.ofType() &&
                storedIn() == item.storedIn() &&
                registers() == item.registers() &&
                buys() == item.buys() &&
                unit() == item.unit();
    }

    @Override
    default void validate() {
        Entity.super.validate();
        Preconditions.checkState(registers() > 0, "registers id is invalid");
        Preconditions.checkState(ofType() > 0, "ofType id is invalid");
        Preconditions.checkState(storedIn() > 0, "storedIn id is invalid");
        Preconditions.checkState(buys() > 0, "buys id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }
}