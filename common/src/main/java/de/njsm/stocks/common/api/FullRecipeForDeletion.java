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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

@AutoValue
@JsonDeserialize(builder = AutoValue_FullRecipeForDeletion.Builder.class)
public abstract class FullRecipeForDeletion implements Versionable<Recipe>, SelfValidating {

    @JsonGetter
    public abstract RecipeForDeletion recipe();

    @JsonGetter
    public abstract ImmutableSet<RecipeIngredientForDeletion> ingredients();

    @JsonGetter
    public abstract ImmutableSet<RecipeProductForDeletion> products();

    public static Builder builder() {
        return new AutoValue_FullRecipeForDeletion.Builder();
    }

    @Override
    @JsonIgnore
    public int id() {
        return recipe().id();
    }

    @Override
    @JsonIgnore
    public int version() {
        return recipe().version();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<FullRecipeForDeletion> {
        public abstract Builder recipe(RecipeForDeletion v);

        public abstract Builder ingredients(Set<RecipeIngredientForDeletion> v);

        public abstract Builder products(Set<RecipeProductForDeletion> v);
    }

    @Override
    public void validate() {
        recipe().validate();
        ingredients().forEach(RecipeIngredientForDeletion::validate);
        products().forEach(RecipeProductForDeletion::validate);
    }
}
