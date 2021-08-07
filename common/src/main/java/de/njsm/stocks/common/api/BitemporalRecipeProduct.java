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

public class BitemporalRecipeProduct extends BitemporalData implements Bitemporal<RecipeProduct>, RecipeProduct {

    private final int amount;

    private final int product;

    private final int recipe;

    private final int unit;

    public BitemporalRecipeProduct(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, int amount, int product, int recipe, int unit) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.amount = amount;
        this.product = product;
        this.recipe = recipe;
        this.unit = unit;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public int product() {
        return product;
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
        if (!(o instanceof BitemporalRecipeProduct)) return false;
        if (!super.equals(o)) return false;
        BitemporalRecipeProduct that = (BitemporalRecipeProduct) o;
        return amount() == that.amount() && product() == that.product() && recipe() == that.recipe() && unit() == that.unit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount(), product(), recipe(), unit());
    }

    @Override
    public boolean isContainedIn(RecipeProduct item) {
        return Bitemporal.super.isContainedIn(item) &&
                amount() == item.amount() &&
                product() == item.product() &&
                recipe() == item.recipe() &&
                unit() == item.unit();
    }

    @Override
    public void validate() {
        Bitemporal.super.validate();
        RecipeProduct.super.validate();
    }
}
