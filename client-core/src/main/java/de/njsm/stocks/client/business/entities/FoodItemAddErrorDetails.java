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
import java.time.LocalDate;

@AutoValue
public abstract class FoodItemAddErrorDetails implements ErrorDetails {

    public abstract LocalDate eatBy();

    public abstract int ofType();

    public abstract int storedIn();

    public abstract int unitId();

    public abstract Unit unit();

    public abstract String foodName();

    public abstract String locationName();

    public static FoodItemAddErrorDetails create(LocalDate eatBy, int ofType, int storedIn, int unitId, Unit unit, String foodName, String locationName) {
        return new AutoValue_FoodItemAddErrorDetails(eatBy, ofType, storedIn, unitId, unit, foodName, locationName);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.foodItemAddErrorDetails(this, input);
    }

    public FoodItemForm into() {
        return FoodItemForm.create(
                eatBy(),
                ofType(),
                storedIn(),
                unitId()
        );
    }

    @AutoValue
    public static abstract class Unit implements ScaledUnitSummaryFields {

        @Override
        @Memoized
        public UnitPrefix decimalPrefix() {
            return ScaledUnitSummaryFields.super.decimalPrefix();
        }

        @Override
        @Memoized
        public BigDecimal prefixedAmount() {
            return ScaledUnitSummaryFields.super.prefixedAmount();
        }

        public static FoodItemAddErrorDetails.Unit create(BigDecimal scale, String abbreviation) {
            return new AutoValue_FoodItemAddErrorDetails_Unit(scale, abbreviation);
        }
    }
}
