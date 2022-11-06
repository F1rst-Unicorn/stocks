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
import com.google.auto.value.extension.memoized.Memoized;

import java.math.BigDecimal;
import java.util.List;

@AutoValue
public abstract class RecipeIngredientAmount {

    public abstract int recipe();

    public abstract Amount requiredAmount();

    public abstract List<Amount> presentAmounts();

    @Memoized
    public boolean isSufficientAmountPresent() {
        return isNecessaryAmountPresent() &&
                presentAmounts().stream()
                        .filter(v -> v.unit() == requiredAmount().unit())
                        .map(Amount::scaledAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .compareTo(requiredAmount().scaledAmount()) >= 0;
    }

    @Memoized
    public boolean isNecessaryAmountPresent() {
        return !presentAmounts().isEmpty();
    }

    public static RecipeIngredientAmount create(int recipe, Amount requiredAmount, List<Amount> presentAmounts) {
        return new AutoValue_RecipeIngredientAmount(recipe, requiredAmount, presentAmounts);
    }

    @AutoValue
    public abstract static class Amount {

        public abstract int unit();

        public abstract BigDecimal scale();

        public abstract int amount();

        @Memoized
        public BigDecimal scaledAmount() {
            return scale().multiply(BigDecimal.valueOf(amount()));
        }

        public static Amount create(int unit, BigDecimal scale, int amount) {
            return new AutoValue_RecipeIngredientAmount_Amount(unit, scale, amount);
        }
    }
}
