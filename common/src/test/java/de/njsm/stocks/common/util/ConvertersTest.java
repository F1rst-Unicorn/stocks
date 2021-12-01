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

package de.njsm.stocks.common.util;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.njsm.stocks.common.util.Converters.bigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;

public class ConvertersTest {

    @Test
    public void invalidBigDecimalIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> bigDecimal(null, ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("null", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("hi there", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("';select * from user", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("1.O", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("five", ""));
        assertThrows(IllegalArgumentException.class, () -> bigDecimal("42.424242424242424242", ""));
    }

    @Test
    void validBigDecimalIsAccepted() {
        assertThat(BigDecimal.ONE, is(closeTo(bigDecimal("1", ""), BigDecimal.ZERO)));
        assertThat(BigDecimal.ONE, is(closeTo(bigDecimal("1.0", ""), BigDecimal.ZERO)));
        String longNumber = "42.42424242424242424";
        assertThat(new BigDecimal(longNumber), is(closeTo(bigDecimal(longNumber, ""), BigDecimal.ZERO)));
    }
}
