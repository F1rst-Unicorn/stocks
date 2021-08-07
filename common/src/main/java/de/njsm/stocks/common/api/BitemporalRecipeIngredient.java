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

import java.time.Instant;
import java.util.Objects;

public class BitemporalRecipeIngredient extends BitemporalData implements Bitemporal<RecipeIngredient>, RecipeIngredient {

    private final int amount;

    private final int ingredient;

    private final int recipe;

    private final int unit;

    public BitemporalRecipeIngredient(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, int amount, int ingredient, int recipe, int unit) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.amount = amount;
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.unit = unit;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public int ingredient() {
        return ingredient;
    }

    @Override
    public int recipe() {
        return recipe;
    }

    @Override
    public int unit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitemporalRecipeIngredient)) return false;
        if (!super.equals(o)) return false;
        BitemporalRecipeIngredient that = (BitemporalRecipeIngredient) o;
        return amount() == that.amount() && ingredient() == that.ingredient() && recipe() == that.recipe() && unit() == that.unit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount(), ingredient(), recipe(), unit());
    }

    @Override
    public boolean isContainedIn(RecipeIngredient item) {
        return Bitemporal.super.isContainedIn(item) &&
                amount() == item.amount() &&
                ingredient() == item.ingredient() &&
                recipe() == item.recipe() &&
                unit() == item.unit();
    }
}
