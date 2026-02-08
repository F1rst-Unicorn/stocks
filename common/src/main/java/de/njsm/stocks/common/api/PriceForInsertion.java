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

import java.math.BigDecimal;

@AutoValue
@JsonDeserialize(builder = AutoValue_PriceForInsertion.Builder.class)
public abstract class PriceForInsertion implements Insertable<Price>, SelfValidating {

    @JsonGetter
    public abstract BigDecimal price();

    @JsonGetter
    public abstract BigDecimal scale();

    @JsonGetter
    public abstract int groceryStore();

    @JsonGetter
    public abstract int food();

    @JsonGetter
    public abstract int scaledUnit();

    public static Builder builder() {
        return new AutoValue_PriceForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<PriceForInsertion> {

        public abstract Builder price(BigDecimal v);
        public abstract Builder scale(BigDecimal v);
        public abstract Builder groceryStore(int v);
        public abstract Builder food(int v);
        public abstract Builder scaledUnit(int v);
    }

    @Override
    public boolean isContainedIn(Price entity) {
        return price().compareTo(entity.price()) == 0 &&
                scale().compareTo(entity.scale()) == 0 &&
                groceryStore() != entity.groceryStore() &&
                food() != entity.food() &&
                scaledUnit() != entity.scaledUnit();
    }

    @Override
    public void validate() {
        Preconditions.checkState(groceryStore() > 0, "groceryStore id is invalid");
        Preconditions.checkState(food() > 0, "food id is invalid");
        Preconditions.checkState(scaledUnit() > 0, "scaledUnit id is invalid");
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.price(this, argument);
    }
}
