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
import de.njsm.stocks.common.api.serialisers.DurationDeserialiser;
import de.njsm.stocks.common.api.serialisers.DurationSerialiser;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class BitemporalRecipe extends BitemporalData implements Bitemporal<Recipe>, Recipe {

    private final String name;

    private final String instructions;

    @JsonSerialize(using = DurationSerialiser.class)
    @JsonDeserialize(using = DurationDeserialiser.class)
    private final Duration duration;

    public BitemporalRecipe(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, String name, String instructions, Duration duration) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String instructions() {
        return instructions;
    }

    @Override
    public Duration duration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitemporalRecipe)) return false;
        if (!super.equals(o)) return false;
        BitemporalRecipe that = (BitemporalRecipe) o;
        return name().equals(that.name()) && instructions().equals(that.instructions()) && duration().equals(that.duration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name(), instructions(), duration());
    }

    @Override
    public boolean isContainedIn(Recipe entity) {
        return Bitemporal.super.isContainedIn(entity) &&
                name.equals(entity.name()) &&
                instructions.equals(entity.instructions()) &&
                duration.equals(entity.duration());
    }
}
