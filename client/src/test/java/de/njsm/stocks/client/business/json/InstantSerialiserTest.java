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

package de.njsm.stocks.client.business.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InstantSerialiserTest {

    private InstantSerialiser uut;

    private JsonGenerator generatorMock;

    @Before
    public void setup() throws Exception {
        generatorMock = mock(JsonGenerator.class);
        uut = new InstantSerialiser();
    }

    @Test
    public void instantIsSerialisedWithoutModification() throws Exception {
        String expected = "1970.01.01-00:00:00.000000-+0000";
        Instant input = Instant.ofEpochMilli(0);

        uut.serialize(input, generatorMock, null);

        verify(generatorMock).writeString(expected);
    }
}
