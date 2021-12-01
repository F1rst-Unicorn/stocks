/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import de.njsm.stocks.common.api.serialisers.PeriodDeserialiser;
import de.njsm.stocks.common.api.serialisers.PeriodSerialiser;

import javax.annotation.Nullable;
import java.time.Period;

public interface Food extends Entity<Food> {

    @JsonGetter
    String name();

    @JsonGetter
    @JsonSerialize(using = PeriodSerialiser.class)
    Period expirationOffset();

    @JsonGetter
    @Nullable
    Integer location();

    @JsonGetter
    boolean toBuy();

    @JsonGetter
    String description();

    @JsonGetter
    int storeUnit();

    interface Builder<T> {

        T name(String v);

        @JsonDeserialize(using = PeriodDeserialiser.class)
        T expirationOffset(Period v);

        T location(Integer v);

        T toBuy(boolean v);

        T description(String v);

        T storeUnit(int v);
    }

    @Override
    default boolean isContainedIn(Food entity, boolean increment) {
        return Entity.super.isContainedIn(entity, increment) &&
                name().equals(entity.name()) &&
                expirationOffset().equals(entity.expirationOffset()) &&
                location().equals(entity.location()) &&
                toBuy() == entity.toBuy() &&
                description().equals(entity.description()) &&
                storeUnit() == entity.storeUnit();
    }

    @Override
    default void validate() {
        Entity.super.validate();
        Preconditions.checkState(!expirationOffset().isNegative(), "expiration offset is negative");
        Preconditions.checkState(location() == null || location() >= 0, "location id is invalid");
        Preconditions.checkState(storeUnit() > 0, "store unit id is invalid");
    }
}
