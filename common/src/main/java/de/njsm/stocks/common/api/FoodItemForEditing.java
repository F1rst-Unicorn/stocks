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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_FoodItemForEditing.class)
public abstract class FoodItemForEditing implements Versionable<FoodItem> {

    public static Builder builder() {
        return new AutoValue_FoodItemForEditing.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<FoodItemForEditing>
            implements Versionable.Builder<Builder> {

        public abstract Builder eatBy(Instant v);

        public abstract Builder storedIn(int v);

        public abstract Builder unit(@Nullable Integer v);
    }

    public abstract Instant eatBy();

    public abstract int storedIn();

    public abstract Optional<Integer> unit();

    @Override
    public boolean isContainedIn(FoodItem item, boolean increment) {
        return Versionable.super.isContainedIn(item, increment) &&
                eatBy().equals(item.eatByDate()) &&
                storedIn() == item.storedIn() &&
                unit().map(v -> v.equals(item.unit())).orElse(true);
    }
}
