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
import java.time.Period;
import java.util.Optional;

@AutoValue
public abstract class FoodAddErrorDetails implements FoodFields, ErrorDetails {

    public abstract String locationName();

    public abstract StoreUnit storeUnitEntity();

    public static FoodAddErrorDetails create(String name,
                                             boolean toBuy,
                                             Period expirationOffset,
                                             Integer location,
                                             int storeUnit,
                                             String description,
                                             String locationName,
                                             FoodAddErrorDetails.StoreUnit storeUnitEntity) {
        return new AutoValue_FoodAddErrorDetails(name, toBuy, expirationOffset, Optional.ofNullable(location), storeUnit, description, locationName, storeUnitEntity);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.foodAddErrorDetails(this, input);
    }

    @AutoValue
    public static abstract class StoreUnit implements ScaledUnitSummaryFields {

        @Override
        @Memoized
        public UnitPrefix unitPrefix() {
            return ScaledUnitSummaryFields.super.unitPrefix();
        }

        @Override
        @Memoized
        public BigDecimal prefixedScale() {
            return ScaledUnitSummaryFields.super.prefixedScale();
        }

        public static StoreUnit create(BigDecimal scale, String abbreviation) {
            return new AutoValue_FoodAddErrorDetails_StoreUnit(scale, abbreviation);
        }
    }
}
