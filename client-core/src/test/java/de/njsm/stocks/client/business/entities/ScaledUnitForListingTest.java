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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.njsm.stocks.client.business.entities.ScaledUnitForListing.create;
import static de.njsm.stocks.client.business.entities.UnitPrefix.*;
import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ScaledUnitForListingTest {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Test
    void noneScaleIsComputed() {
        ScaledUnitForListing uut = create(1, "g", ZERO);
        assertEquals(UnitPrefix.NONE, uut.unitPrefix());
        assertEquals(ZERO, uut.prefixedScale());

        testOneUnitPrefix(ONE, NONE);
    }

    @Test
    void kiloScaleIsComputed() {
        testOneUnitPrefix(THOUSAND, KILO);
    }

    @Test
    void milliScaleIsComputed() {
        testOneUnitPrefix(ONE.divide(THOUSAND), MILLI);
    }

    @Test
    void yoctoScaleIsComputed() {
        testOneUnitPrefix(ONE.divide(TEN.pow(24)), YOCTO);
    }

    @Test
    void yottaScaleIsComputed() {
        testOneUnitPrefix(TEN.pow(24), YOTTA);
    }

    private void testOneUnitPrefix(BigDecimal base, UnitPrefix expected) {
        ScaledUnitForListing uut = create(1, "g", base);
        assertEquals(expected, uut.unitPrefix());
        assertEquals(0, ONE.compareTo(uut.prefixedScale()), base + " is different from " + uut.prefixedScale());

        uut = create(1, "g", base.multiply(TEN));
        assertEquals(expected, uut.unitPrefix());
        assertEquals(0, TEN.compareTo(uut.prefixedScale()), base.multiply(TEN) + " is different from " + uut.prefixedScale());

        uut = create(1, "g", base.multiply(HUNDRED));
        assertEquals(expected, uut.unitPrefix());
        assertEquals(0, HUNDRED.compareTo(uut.prefixedScale()), base.multiply(HUNDRED) + " is different from " + uut.prefixedScale());

        uut = create(1, "g", base.multiply(THOUSAND));
        assertNotEquals(expected, uut.unitPrefix());

        if (uut.unitPrefix() != OTHER)  // OTHER doesn't scale correctly
            assertEquals(0, ONE.compareTo(uut.prefixedScale()), ONE + " is different from " + uut.prefixedScale());
    }
}
