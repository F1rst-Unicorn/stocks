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
import de.njsm.stocks.common.api.serialisers.DurationDeserialiser;
import de.njsm.stocks.common.api.serialisers.DurationSerialiser;

import java.time.Duration;

public interface Recipe extends Entity<Recipe> {

    @JsonGetter
    String name();

    @JsonGetter
    String instructions();

    @JsonGetter
    @JsonSerialize(using = DurationSerialiser.class)
    Duration duration();

    @Override
    default boolean isContainedIn(Recipe entity) {
        return Entity.super.isContainedIn(entity) &&
                name().equals(entity.name()) &&
                instructions().equals(entity.instructions()) &&
                duration().equals(entity.duration());
    }

    interface Builder<T> {

        T name(String v);

        T instructions(String v);

        @JsonDeserialize(using = DurationDeserialiser.class)
        T duration(Duration v);
    }

    @Override
    default void validate() {
        Entity.super.validate();
    }
}
