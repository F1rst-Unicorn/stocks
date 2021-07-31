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

package de.njsm.stocks.common.api.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.Recipe;
import de.njsm.stocks.common.api.serialisers.DurationDeserialiser;
import de.njsm.stocks.common.api.serialisers.DurationSerialiser;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import java.time.Duration;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeForInsertion.Builder.class)
public abstract class RecipeForInsertion implements Insertable<Recipe>, SelfValidating {

    @JsonGetter
    public abstract String name();

    @JsonGetter
    public abstract String instructions();

    @JsonGetter
    @JsonSerialize(using = DurationSerialiser.class)
    public abstract Duration duration();

    public static Builder builder() {
        return new AutoValue_RecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<RecipeForInsertion> {

        public abstract Builder name(String v);

        public abstract Builder instructions(String v);

        @JsonDeserialize(using = DurationDeserialiser.class)
        public abstract Builder duration(Duration v);
    }

    @Override
    public boolean isContainedIn(Recipe entity) {
        return name().equals(entity.getName()) &&
                instructions().equals(entity.getInstructions()) &&
                duration().equals(entity.getDuration());
    }

    @Override
    public void validate() {
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.recipeForInsertion(this, argument);
    }
}
