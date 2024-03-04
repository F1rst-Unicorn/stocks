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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.math.BigDecimal;

import static de.njsm.stocks.client.business.entities.IdImpl.from;

@AutoValue
public abstract class RecipeCookingFormDataProduct implements Serializable {

    private static final long serialVersionUID = 1;

    public abstract IdImpl<Food> id();

    public abstract String name();

    public abstract Amount producedAmount();

    public static RecipeCookingFormDataProduct create(Id<Food> id, String name, Amount producedAmount) {
        return new AutoValue_RecipeCookingFormDataProduct(from(id), name, producedAmount);
    }

    public RecipeCookingFormDataProduct mergeFrom(RecipeCookingFormDataProduct presentProduct) {
        if (presentProduct == null)
            return this;

        if (presentProduct.producedAmount().id().equals(producedAmount().id()))
            return create(id(), name(), presentProduct.producedAmount());
        else
            return this;
    }

    public RecipeCookingProductToProduce toProductions() {
        return RecipeCookingProductToProduce.create(
                id(),
                producedAmount().id(),
                producedAmount().producedAmount());
    }

    @AutoValue
    public abstract static class Amount implements UnitAmount, Serializable {

        private static final long serialVersionUID = 1;

        public abstract IdImpl<ScaledUnit> id();

        public abstract int producedAmount();

        public Amount increase() {
            return create(id(), amount(), abbreviation(), producedAmount() + 1);
        }

        public Amount decrease() {
            if (producedAmount() <= 0) {
                return this;
            }
            return create(id(), amount(), abbreviation(), producedAmount() - 1);
        }

        public BigDecimal scaledProductedAmount() {
            return prefixedAmount().multiply(new BigDecimal(producedAmount()));
        }

        public static Amount create(Id<ScaledUnit> id, BigDecimal amount, String abbreviation, int producedAmount) {
            return new AutoValue_RecipeCookingFormDataProduct_Amount(amount, abbreviation, from(id), producedAmount);
        }
    }
}
