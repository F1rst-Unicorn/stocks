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

import java.math.BigDecimal;

import static de.njsm.stocks.client.business.entities.IdImpl.from;

@AutoValue
public abstract class RecipeCookingFormDataProduct {

    public abstract IdImpl<Food> id();

    public abstract String name();

    public abstract Amount producedAmount();

    public static RecipeCookingFormDataProduct create(Id<Food> id, String name, Amount producedAmount) {
        return new AutoValue_RecipeCookingFormDataProduct(from(id), name, producedAmount);
    }

    @AutoValue
    public static abstract class Amount implements UnitAmount {

        public abstract IdImpl<ScaledUnit> id();

        public abstract int defaultProducedAmount();

        public BigDecimal scaledDefaultProductedAmount() {
            return prefixedAmount().multiply(new BigDecimal(defaultProducedAmount()));
        }

        public static Amount create(Id<ScaledUnit> id, BigDecimal amount, String abbreviation, int defaultProducedAmount) {
            return new AutoValue_RecipeCookingFormDataProduct_Amount(amount, abbreviation, from(id), defaultProducedAmount);
        }
    }
}
