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

package de.njsm.stocks.client.business;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.Unit;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

import static de.njsm.stocks.client.business.entities.IdImpl.from;
import static java.math.BigDecimal.valueOf;

class RecipeIngredientAmountDistributor {

    @Inject
    RecipeIngredientAmountDistributor() {
    }

    Map<IdImpl<ScaledUnit>, Integer> distribute(RequiredAmount requiredAmount, List<PresentAmount> presentAmounts) {
        Map<IdImpl<ScaledUnit>, Integer> result = new TreeMap<>(Comparator.comparing(IdImpl::id));

        List<PresentAmount> amountsOfSameUnit = new ArrayList<>();

        for (PresentAmount amount : presentAmounts) {
            if (amount.unit().equals(requiredAmount.unit())) {
                amountsOfSameUnit.add(amount);
            } else {
                result.put(amount.scaledUnit(), 0);
            }
        }

        amountsOfSameUnit.sort((o1, o2) -> - o1.scale().compareTo(o2.scale()));
        BigDecimal remainingRequiredAmount = requiredAmount.scale();

        for (PresentAmount amount : amountsOfSameUnit) {
            int requiredItems = remainingRequiredAmount.divideToIntegralValue(amount.scale()).intValue();
            int feasibleItems = Integer.min(requiredItems, amount.presentItemCount());
            result.put(amount.scaledUnit(), feasibleItems);
            remainingRequiredAmount = remainingRequiredAmount.subtract(amount.scale().multiply(valueOf(feasibleItems)));
        }

        return result;
    }

    @AutoValue
    public abstract static class RequiredAmount {
        public abstract IdImpl<Unit> unit();
        public abstract BigDecimal scale();
        public static RequiredAmount create(Id<Unit> unit, BigDecimal scale) {
            return new AutoValue_RecipeIngredientAmountDistributor_RequiredAmount(from(unit), scale);
        }
    }

    @AutoValue
    public abstract static class PresentAmount {
        public abstract IdImpl<Unit> unit();
        public abstract IdImpl<ScaledUnit> scaledUnit();
        public abstract BigDecimal scale();
        public abstract int presentItemCount();

        public static PresentAmount create(Id<Unit> unit, Id<ScaledUnit> scaledUnit, BigDecimal scale, int presentCount) {
            return new AutoValue_RecipeIngredientAmountDistributor_PresentAmount(from(unit), from(scaledUnit), scale, presentCount);
        }
    }
}
