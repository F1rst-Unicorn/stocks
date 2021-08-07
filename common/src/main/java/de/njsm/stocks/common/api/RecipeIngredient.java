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
import com.google.common.base.Preconditions;

public interface RecipeIngredient extends Entity<RecipeIngredient> {

    @JsonGetter
    int amount();

    @JsonGetter
    int ingredient();

    @JsonGetter
    int recipe();

    @JsonGetter
    int unit();

    @Override
    default void validate() {
        Entity.super.validate();
        Preconditions.checkState(ingredient() > 0, "ingredient id is invalid");
        Preconditions.checkState(recipe() > 0, "recipe id is invalid");
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }

    @Override
    default boolean isContainedIn(RecipeIngredient item) {
        return Entity.super.isContainedIn(item) &&
                amount() == item.amount() &&
                ingredient() == item.ingredient() &&
                recipe() == item.recipe() &&
                unit() == item.unit();
    }

    interface Builder<T> {
        T amount(int v);

        T ingredient(int v);

        T recipe(int v);

        T unit(int v);
    }
}
