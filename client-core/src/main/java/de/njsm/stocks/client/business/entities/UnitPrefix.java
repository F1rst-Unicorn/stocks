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

import java.math.BigDecimal;

import static java.lang.Math.abs;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

public enum UnitPrefix {
    YOTTA ("Y", 24),
    ZETTA ("Z", 21),
    EXA ("E", 18),
    PETA ("P", 15),
    TERA ("T", 12),
    GIGA ("G", 9),
    MEGA ("M", 6),
    KILO ("k", 3),
    NONE ("", 0),
    MILLI ("m", -3),
    MICRO ("\u00b5", -6),
    NANO ("n", -9),
    PICO ("p", -12),
    FEMTO ("f", -15),
    ATTO ("a", -18),
    ZEPTO ("z", -21),
    YOCTO ("y", -24),
    OTHER("?", 0);

    public static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

    private final String symbol;

    private final int exponent;

    UnitPrefix(String symbol, int exponent) {
        this.symbol = symbol;
        this.exponent = exponent;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getExponent() {
        return exponent;
    }

    public BigDecimal getFactor() {
        if (getExponent() < 0)
            return ONE.divide(TEN.pow(abs(getExponent())));
        else
            return TEN.pow(getExponent());
    }
}
