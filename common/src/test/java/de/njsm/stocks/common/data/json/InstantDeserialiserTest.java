/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.common.data.json;

import org.junit.Before;
import org.junit.Test;

import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InstantDeserialiserTest {

    private InstantDeserialiser uut;

    @Before
    public void setup() throws Exception {
        uut = new InstantDeserialiser();
    }

    @Test
    public void invalidInputThrowsException() throws Exception {
        try {
            uut.parseString("fdsaklföe");
            fail();
        } catch (DateTimeParseException e) {

        }
    }

    @Test
    public void validInputIsParsedWithoutModification() throws Exception {
        String input = "1970.01.01-00:00:00.000-+0000";
        Instant expected = Instant.ofEpochMilli(0);

        Instant actual = uut.parseString(input);

        assertEquals(expected, actual);
    }
}