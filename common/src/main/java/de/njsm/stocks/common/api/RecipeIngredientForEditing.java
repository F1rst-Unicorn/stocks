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
@JsonDeserialize(builder = AutoValue_RecipeIngredientForEditing.Builder.class)
public abstract class RecipeIngredientForEditing implements RecipeIngredientWithRecipeIdData, Versionable<RecipeIngredient> {

    public static Builder builder() {
        return new AutoValue_RecipeIngredientForEditing.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder
            extends SelfValidating.Builder<RecipeIngredientForEditing>
            implements RecipeIngredientWithRecipeIdData.Builder<Builder>, Versionable.Builder<Builder> {
    }

    @Override
    public void validate() {
        Versionable.super.validate();
        Preconditions.checkState(ingredient() > 0, "ingredient id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
        Preconditions.checkState(recipe() > 0, "recipe id is invalid");
    }

    @Override
    public boolean isContainedIn(RecipeIngredient item, boolean increment) {
        return Versionable.super.isContainedIn(item, increment) &&
                ingredient() == item.ingredient() &&
                amount() == item.amount() &&
                recipe() == item.recipe() &&
                unit() == item.unit();
    }
}
