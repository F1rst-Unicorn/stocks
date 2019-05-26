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

package de.njsm.stocks.server.v2.business.json;

import de.njsm.stocks.server.v2.business.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatusCodeDeserialiserTest {

    private StatusCodeDeserialiser uut;

    @Before
    public void setup() {
        uut = new StatusCodeDeserialiser();
    }

    @Test
    public void invalidInputThrowsException() {
        try {
            uut.parseCode(-1);
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    public void validInputIsParsedWithoutModification() throws IOException {
        StatusCode expected = StatusCode.SUCCESS;

        StatusCode actual = uut.parseCode(expected.ordinal());

        assertEquals(expected, actual);
    }
}