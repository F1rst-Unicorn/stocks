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
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.visitor.BitemporalVisitor;

@AutoValue
@JsonDeserialize(builder = AutoValue_BitemporalLocation.Builder.class)
public abstract class BitemporalLocation implements Bitemporal<Location>, Location {

    public static Builder builder() {
        return new AutoValue_BitemporalLocation.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<BitemporalLocation>
            implements Bitemporal.Builder<Builder>, Location.Builder<Builder> {
    }

    @Override
    public boolean isContainedIn(Location item, boolean increment) {
        return Bitemporal.super.isContainedIn(item, increment) &&
                Location.super.isContainedIn(item, increment);
    }

    @Override
    public void validate() {
        Bitemporal.super.validate();
        Location.super.validate();
    }

    @Override
    public <I, O> O accept(BitemporalVisitor<I, O> visitor, I data) {
        return visitor.bitemporalLocation(this, data);
    }
}
