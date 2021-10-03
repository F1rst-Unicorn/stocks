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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_FullRecipeForInsertion.Builder.class)
public abstract class FullRecipeForInsertion implements SelfValidating {

    @JsonGetter
    public abstract RecipeForInsertion recipe();

    @JsonGetter
    public abstract ImmutableList<RecipeIngredientForInsertion> ingredients();

    @JsonGetter
    public abstract ImmutableList<RecipeProductForInsertion> products();

    public static Builder builder() {
        return new AutoValue_FullRecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<FullRecipeForInsertion> {
        public abstract Builder recipe(RecipeForInsertion v);

        public abstract Builder ingredients(List<RecipeIngredientForInsertion> v);

        public abstract ImmutableList.Builder<RecipeIngredientForInsertion> ingredientsBuilder();

        public Builder addIngredient(RecipeIngredientForInsertion v) {
            ingredientsBuilder().add(v);
            return this;
        }

        public abstract Builder products(List<RecipeProductForInsertion> v);

        public abstract ImmutableList.Builder<RecipeProductForInsertion> productsBuilder();

        public Builder addProduct(RecipeProductForInsertion v) {
            productsBuilder().add(v);
            return this;
        }
    }

    @Override
    public void validate() {
        recipe().validate();
        ingredients().forEach(RecipeIngredientForInsertion::validate);
        products().forEach(RecipeProductForInsertion::validate);
    }
}
