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

@AutoValue
@JsonDeserialize(builder = AutoValue_BitemporalScaledUnit.Builder.class)
public abstract class BitemporalScaledUnit implements Bitemporal<ScaledUnit>, ScaledUnit {

    public static BitemporalScaledUnit.Builder builder() {
        return new AutoValue_BitemporalScaledUnit.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<BitemporalScaledUnit>
            implements Bitemporal.Builder<Builder>, ScaledUnit.Builder<Builder> {
    }

    @Override
    public boolean isContainedIn(ScaledUnit entity, boolean increment) {
        return Bitemporal.super.isContainedIn(entity, increment) &&
                ScaledUnit.super.isContainedIn(entity, increment);
    }

    @Override
    public void validate() {
        Bitemporal.super.validate();
        ScaledUnit.super.validate();
    }
}
