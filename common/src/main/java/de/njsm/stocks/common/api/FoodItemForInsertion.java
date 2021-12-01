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
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_FoodItemForInsertion.Builder.class)
public abstract class FoodItemForInsertion implements Insertable<FoodItem>, SelfValidating {

    @JsonGetter
    public abstract Instant eatByDate();

    @JsonGetter
    public abstract int ofType();

    public Identifiable<Food> ofTypeIdentifiable() {
        return this::ofType;
    }

    @JsonGetter
    public abstract int storedIn();

    @JsonGetter
    public abstract int registers();

    @JsonGetter
    public abstract int buys();

    @JsonGetter
    public abstract Optional<Integer> unit();

    public static Builder builder() {
        return new AutoValue_FoodItemForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<FoodItemForInsertion> {

        public abstract Builder eatByDate(Instant v);

        public abstract Builder ofType(int v);

        public abstract Builder storedIn(int v);

        public abstract Builder registers(int v);

        public abstract Builder buys(int v);

        public abstract Builder unit(@Nullable Integer v);
    }

    @Override
    public boolean isContainedIn(FoodItem entity) {
        return eatByDate().equals(entity.eatByDate()) &&
                ofType() == entity.ofType() &&
                storedIn() == entity.storedIn() &&
                registers() == entity.registers() &&
                buys() == entity.buys() &&
                unit().map(v -> v.equals(entity.unit())).orElse(true);
    }

    @Override
    public void validate() {
        Preconditions.checkState(ofType() > 0, "ofType id is invalid");
        Preconditions.checkState(storedIn() > 0, "storedIn id is invalid");
        Preconditions.checkState(registers() > 0, "registers id is invalid");
        Preconditions.checkState(buys() > 0, "buys id is invalid");
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.foodItemForInsertion(this, argument);
    }
}
