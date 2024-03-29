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
import de.njsm.stocks.client.business.SearchRepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.business.entities.IdImpl.from;
import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.toMap;

@AutoValue
public abstract class RecipeCookingFormDataIngredient implements Serializable {

    private static final long serialVersionUID = 1;

    public abstract IdImpl<Food> id();

    public abstract String name();

    public abstract boolean toBuy();

    public abstract List<Amount> requiredAmount();

    public abstract List<PresentAmount> presentAmount();

    public static RecipeCookingFormDataIngredient create(Id<Food> id, String name, boolean toBuy, List<Amount> requiredAmount, List<PresentAmount> presentAmount) {
        return new AutoValue_RecipeCookingFormDataIngredient(from(id), name, toBuy, requiredAmount, presentAmount);
    }

    RecipeCookingFormDataIngredient mergeFrom(RecipeCookingFormDataIngredient presentIngredient) {
        if (presentIngredient == null)
            return this;

        var amountsByScaledUnit = presentIngredient.presentAmount().stream()
                .collect(toMap(RecipeCookingFormDataIngredient.PresentAmount::scaledUnit, v -> v));

        return create(id(), name(), toBuy(), requiredAmount(), presentAmount().stream()
                .map(v -> {
                    var presentAmount = amountsByScaledUnit.get(v.scaledUnit());
                    if (presentAmount == null)
                        return v;

                    return PresentAmount.create(v.amount(), v.scaledUnit(), v.presentCount(), Math.min(v.presentCount(), presentAmount.selectedCount()));
                })
                .collect(Collectors.toList()));
    }

    public List<RecipeCookingIngredientToConsume> toConsumptions() {
        return presentAmount().stream()
                .filter(v -> v.selectedCount() > 0)
                .map(v -> RecipeCookingIngredientToConsume.create(id(), v.scaledUnit(), v.selectedCount()))
                .collect(Collectors.toList());
    }

    @AutoValue
    public abstract static class PresentAmount implements Serializable {

        private static final long serialVersionUID = 1;

        public abstract Amount amount();

        public abstract IdImpl<ScaledUnit> scaledUnit();

        public abstract int presentCount();

        public abstract int selectedCount();

        public BigDecimal scalePresent() {
            return amount().prefixedAmount().multiply(valueOf(presentCount()));
        }

        public BigDecimal scaleSelected() {
            return amount().prefixedAmount().multiply(valueOf(selectedCount()));
        }

        public PresentAmount increase() {
            if (selectedCount() >= presentCount())
                return this;
            else
                return create(amount(), scaledUnit(), presentCount(), selectedCount() + 1);
        }

        public PresentAmount decrease() {
            if (selectedCount() <= 0)
                return this;
            else
                return create(amount(), scaledUnit(), presentCount(), selectedCount() - 1);
        }

        public static PresentAmount create(Amount amount, IdImpl<ScaledUnit> scaledUnit, int presentCount, int selectedCount) {
            return new AutoValue_RecipeCookingFormDataIngredient_PresentAmount(amount, scaledUnit, presentCount, selectedCount);
        }
    }

    @AutoValue
    public abstract static class Amount implements UnitAmount, Serializable {

        private static final long serialVersionUID = 1;

        public static Amount create(BigDecimal amount, String abbreviation) {
            return new AutoValue_RecipeCookingFormDataIngredient_Amount(amount, abbreviation);
        }
    }
}
