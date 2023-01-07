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

import javax.annotation.Nullable;
import java.time.Period;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_FoodForFullEditing.Builder.class)
public abstract class FoodForFullEditing implements Versionable<Food> {

    @JsonGetter
    public abstract String name();

    @JsonGetter
    public abstract Optional<Integer> expirationOffset();

    @JsonGetter
    public abstract Optional<Boolean> toBuy();

    @JsonGetter
    public abstract Optional<Integer> location();

    @JsonGetter
    public abstract Optional<String> description();

    @JsonGetter
    public abstract Optional<Integer> storeUnit();

    public static Builder builder() {
        return new AutoValue_FoodForFullEditing.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<FoodForFullEditing>
            implements Versionable.Builder<Builder> {

        public abstract Builder name(String v);

        public abstract Builder toBuy(@Nullable Boolean v);

        public abstract Builder expirationOffset(@Nullable Integer v);

        public abstract Builder location(@Nullable Integer v);

        public abstract Builder description(@Nullable String v);

        public abstract Builder storeUnit(@Nullable Integer v);
    }

    @Override
    public void validate() {
        Versionable.super.validate();
        Preconditions.checkState(location().map(v -> v >= 0).orElse(true), "location id is invalid");
        Preconditions.checkState(storeUnit().map(v -> v > 0).orElse(true), "storeUnit id is invalid");
    }

    @Override
    public boolean isContainedIn(Food item, boolean increment) {
        return Versionable.super.isContainedIn(item, increment) &&
                name().equals(item.name()) &&
                location().map(v -> v.equals(item.location()) || (
                        v == 0 && item.location() == null
                        )).orElse(true) &&
                expirationOffset().map(Period::ofDays).map(v -> v.equals(item.expirationOffset())).orElse(true) &&
                toBuy().map(v -> v.equals(item.toBuy())).orElse(true) &&
                description().map(v -> v.equals(item.description())).orElse(true) &&
                storeUnit().map(v -> v.equals(item.storeUnit())).orElse(true);
    }
}
