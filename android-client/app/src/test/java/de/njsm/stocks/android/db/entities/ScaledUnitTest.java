/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.db.entities;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.Locale;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.Assert.assertEquals;

public class ScaledUnitTest {

    @Test
    public void valuesSmallerThanThousandArePreserved() {
        BigDecimal input = ONE;
        assertEquals(input, ScaledUnit.shrinkNumber(input));
    }

    @Test
    public void valueLargerThanThousandIsNormalised() {
        BigDecimal input = new BigDecimal(1000);
        assertEquals(0, ONE.compareTo(ScaledUnit.shrinkNumber(input)));
    }

    @Test
    public void valueLargerThanAMillionIsNormalised() {
        BigDecimal input = new BigDecimal(1000000);
        assertEquals(0, ONE.compareTo(ScaledUnit.shrinkNumber(input)));
    }

    @Test
    public void valueSmallerThanOneIsNormalised() {
        BigDecimal input = new BigDecimal("0.1");
        assertEquals(0, new BigDecimal(100).compareTo(ScaledUnit.shrinkNumber(input)));
    }

    @Test
    public void valueSmallerThanOneOverThousandIsNormalised() {
        BigDecimal input = new BigDecimal("0.0001");
        assertEquals(0, new BigDecimal(100).compareTo(ScaledUnit.shrinkNumber(input)));
    }

    @Test
    public void testYottaPrefix() {
        testMacroPrefix(27, "?");
        testMacroPrefix(24, "Y");
        testMacroPrefix(21, "Z");
        testMacroPrefix(18, "E");
        testMacroPrefix(15, "P");
        testMacroPrefix(12, "T");
        testMacroPrefix(9, "G");
        testMacroPrefix(6, "M");
        testMacroPrefix(3, "k");
        testMacroPrefix(0, "");
    }

    @Test
    public void testMilliPrefix() {
        testMicroPrefix(3, "m");
        testMicroPrefix(6, "μ");
        testMicroPrefix(9, "n");
        testMicroPrefix(12, "p");
        testMicroPrefix(15, "f");
        testMicroPrefix(18, "a");
        testMicroPrefix(21, "z");
        testMicroPrefix(24, "y");
        testMicroPrefix(27, "?");
    }

    @Test
    public void normaliseScaledUnitWorks() {
        ScaledUnit uut = new ScaledUnit(0, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, ONE, 0);
        uut.scale = new BigDecimal(10000);
        assertEquals("10k", uut.normalise());
    }

    @Test
    public void normaliseScaledUnitWithFractionWorks() {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        ScaledUnit uut = new ScaledUnit(0, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, ONE, 0);
        uut.scale = new BigDecimal(10500);
        assertEquals("10.5k", uut.normalise());
        Locale.setDefault(Locale.GERMANY);
        assertEquals("10,5k", uut.normalise());
        Locale.setDefault(defaultLocale);
    }

    private void testMicroPrefix(int i, String m) {
        BigDecimal input = ONE.divide(TEN.pow(i), MathContext.UNLIMITED);
        assertEquals(m, ScaledUnit.getScalePrefix(input));
    }

    private void testMacroPrefix(int i, String y) {
        BigDecimal input = TEN.pow(i);
        assertEquals(y, ScaledUnit.getScalePrefix(input));
    }
}
