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
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;

@AutoValue
@JsonDeserialize(builder = AutoValue_ScaledUnitForEditing.Builder.class)
public abstract class ScaledUnitForEditing implements Versionable<ScaledUnit> {

    @JsonGetter
    public abstract BigDecimal scale();

    @JsonGetter
    public abstract int unit();

    public static Builder builder() {
        return new AutoValue_ScaledUnitForEditing.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<ScaledUnitForEditing>
            implements Versionable.Builder<Builder>, ScaledUnit.ScaleFromString<Builder> {

        public abstract Builder scale(BigDecimal v);

        public abstract Builder unit(int v);
    }

    @Override
    public void validate() {
        Versionable.super.validate();
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }

    @Override
    public boolean isContainedIn(ScaledUnit item, boolean increment) {
        return Versionable.super.isContainedIn(item, increment) &&
                scale().equals(item.scale()) &&
                unit() == item.unit();
    }
}
