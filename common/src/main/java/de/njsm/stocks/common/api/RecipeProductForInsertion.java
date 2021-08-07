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
import com.google.common.base.Preconditions;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeProductForInsertion.Builder.class)
public abstract class RecipeProductForInsertion implements SelfValidating, RecipeProductForInsertionData {

    public static Builder builder() {
        return new AutoValue_RecipeProductForInsertion.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends SelfValidating.Builder<RecipeProductForInsertion> {
        public abstract Builder amount(int v);

        public abstract Builder product(int v);

        public abstract Builder unit(int v);
    }

    public RecipeProductWithIdForInsertion withRecipe(int recipe) {
        return RecipeProductWithIdForInsertion.builder()
                .amount(amount())
                .product(product())
                .unit(unit())
                .recipe(recipe)
                .build();
    }

    @Override
    public void validate() {
        Preconditions.checkState(product() > 0, "product id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }
}
