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

@AutoValue
public abstract class ScaledUnitEditErrorDetails implements ScaledUnitFields, FullScaledUnitSummaryFields, ErrorDetails {

    public abstract int id();

    @Override
    public BigDecimal scale() {
        return FullScaledUnitSummaryFields.super.scale();
    }

    @Override
    @Memoized
    public UnitPrefix decimalPrefix() {
        return FullScaledUnitSummaryFields.super.decimalPrefix();
    }

    @Override
    @Memoized
    public BigDecimal prefixedAmount() {
        return FullScaledUnitSummaryFields.super.prefixedAmount();
    }

    public static ScaledUnitEditErrorDetails create(int id, BigDecimal scale, int unit, String name, String abbreviation) {
        return new AutoValue_ScaledUnitEditErrorDetails(unit, scale, abbreviation, name, id);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.scaledUnitEditErrorDetails(this, input);
    }
}
