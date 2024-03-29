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

import com.google.auto.value.AutoValue;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

@AutoValue
public abstract class RecipeIngredientWithIdForInsertion implements Insertable<RecipeIngredient>, RecipeIngredientWithRecipeIdData {

    public static Builder builder() {
        return new AutoValue_RecipeIngredientWithIdForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder
            implements RecipeIngredientWithRecipeIdData.Builder<Builder> {

        public abstract RecipeIngredientWithIdForInsertion build();
    }

    @Override
    public boolean isContainedIn(RecipeIngredient entity) {
        return RecipeIngredientWithRecipeIdData.super.isContainedIn(entity);
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.recipeIngredientWithIdForInsertion(this, argument);
    }
}
