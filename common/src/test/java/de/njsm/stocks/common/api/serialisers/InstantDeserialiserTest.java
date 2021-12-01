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

package de.njsm.stocks.common.api.serialisers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.njsm.stocks.common.api.Update;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InstantDeserialiserTest {

    @Test
    public void deserialisingWorks() throws JsonProcessingException {
        String input = "{\"table\":\"table\",\"lastUpdate\":\"1970.01.01-00:00:00.000000-+0000\"}";

        Update output = new ObjectMapper().readValue(input, Update.class);

        assertEquals(Instant.EPOCH, output.lastUpdate());
    }

    @Test
    public void invalidInputThrowsException() {
        assertThrows(IOException.class, () -> InstantDeserialiser.parseString("fdsaklföe"));
    }

    @Test
    public void emptyInputThrowsException() {
        assertThrows(IOException.class, () -> InstantDeserialiser.parseString(""));
    }

    @Test
    public void nullInputThrowsException() {
        assertThrows(IOException.class, () -> InstantDeserialiser.parseString(null));
    }

    @Test
    public void validInputIsParsedWithoutModification() throws IOException {
        String input = "1970.01.01-00:00:00.000000-+0000";
        Instant expected = Instant.ofEpochMilli(0);

        Instant actual = InstantDeserialiser.parseString(input);

        assertEquals(expected, actual);
    }
}
