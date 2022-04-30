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
import java.math.MathContext;

import static de.njsm.stocks.client.business.entities.UnitPrefix.OTHER;
import static de.njsm.stocks.client.business.entities.UnitPrefix.THOUSAND;
import static java.math.BigDecimal.ZERO;

@AutoValue
public abstract class ScaledUnitForListing implements Identifiable<ScaledUnit> {

    public abstract String abbreviation();

    public abstract BigDecimal scale();

    @Memoized
    public UnitPrefix unitPrefix() {
        return computeUnitPrefix(scale());
    }

    @Memoized
    public BigDecimal prefixedScale() {
        return scale().divide(unitPrefix().getFactor());
    }

    public static ScaledUnitForListing create(int id, String abbreviation, BigDecimal scale) {
        return new AutoValue_ScaledUnitForListing(id, abbreviation, scale);
    }

    private static UnitPrefix computeUnitPrefix(BigDecimal input) {
        int exponent = computeExponent(input);
        int index = -exponent + 8;

        if (0 <= index && index < UnitPrefix.values().length - 1)
            return UnitPrefix.values()[index];
        else
            return OTHER;
    }

    private static int computeExponent(BigDecimal input) {
        int result = 0;
        if (input.compareTo(ZERO) == 0)
            return result;

        BigDecimal iterator = input;
        while (iterator.compareTo(THOUSAND) >= 0) {
            result++;
            iterator = iterator.divide(THOUSAND, MathContext.UNLIMITED);
        }

        while (iterator.compareTo(BigDecimal.ONE) < 0) {
            result--;
            iterator = iterator.multiply(THOUSAND);
        }

        return result;
    }
}
