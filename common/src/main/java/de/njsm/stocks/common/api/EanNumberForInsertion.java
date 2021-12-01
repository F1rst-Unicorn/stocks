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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;


@AutoValue
@JsonDeserialize(builder = AutoValue_EanNumberForInsertion.Builder.class)
public abstract class EanNumberForInsertion implements Insertable<EanNumber>, SelfValidating {

    @JsonGetter
    public abstract int identifiesFood();

    @JsonGetter
    public abstract String eanNumber();

    public static Builder builder() {
        return new AutoValue_EanNumberForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<EanNumberForInsertion> {

        public abstract Builder identifiesFood(int v);

        @JsonSetter("eanCode")
        public abstract Builder eanNumber(String v);
    }

    @Override
    public boolean isContainedIn(EanNumber entity) {
        return identifiesFood() == entity.identifiesFood() &&
                eanNumber().equals(entity.eanNumber());
    }

    @Override
    public void validate() {
        Preconditions.checkState(identifiesFood() > 0, "identifiesFood id is invalid");
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.eanNumberForInsertion(this, argument);
    }
}
