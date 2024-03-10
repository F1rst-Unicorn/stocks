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

package de.njsm.stocks.server.v2.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class EndpointTest {

    private Endpoint uut;

    private static final String RAW_INSTANT = "1970.01.01-00:00:00.000000-+0000";

    @BeforeEach
    public void setup() {
        uut = new Endpoint();
    }

    @Test
    public void positiveIdsAreValid() {
        for (int i = 1; i < 100; i++) {
            assertTrue(uut.isValid(i, "value name"), i + " is considered invalid");
        }
    }

    @Test
    public void negativeIdsAreInvalid() {
        for (int i = -100; i < 0; i++) {
            assertFalse(uut.isValid(i, "value name"), i + " is considered invalid");
        }
    }

    @Test
    public void zeroIsInvalidId() {
        assertFalse(uut.isValid(0, "value name"));
    }

    @Test
    public void positiveVersionsAreValid() {
        for (int i = 1; i < 100; i++) {
            assertTrue(uut.isValid(i, "value name"), i + " is considered invalid");
        }
    }

    @Test
    public void negativeVersionsAreInvalid() {
        for (int i = -100; i < 0; i++) {
            assertFalse(uut.isValid(i, "value name"), i + " is considered invalid");
        }
    }

    @Test
    public void zeroIsValidVersion() {
        assertTrue(uut.isValidVersion(0, "value name"));
    }

    @Test
    public void nullStringIsInvalid() {
        assertFalse(uut.isValid(null, "value name"));
    }

    @Test
    public void emptyStringIsInvalid() {
        assertFalse(uut.isValid("", "value name"));
    }

    @Test
    public void otherStringsAreValid() {
        assertTrue(uut.isValid("a", "value name"));
        assertTrue(uut.isValid("aa", "value name"));
        assertTrue(uut.isValid("Banana", "value name"));
        assertTrue(uut.isValid("fdsa", "value name"));
        assertTrue(uut.isValid("Fridge", "value name"));
        assertTrue(uut.isValid("Jack", "value name"));
        assertTrue(uut.isValid("-----BEGIN CERTIFICATE REQUEST-----\n", "value name"));
        assertTrue(uut.isValid("CMV7oaD99ElqaOX8urfgRFTqClPlRag9XpyLgiSHg/18UwIDAQABoAAwDQYJKoZI", "value name"));
    }

    @Test
    public void testEmptyStringValidator() {
        assertTrue(uut.isValidOrEmpty("", "value name"));
        assertTrue(uut.isValidOrEmpty("content", "value name"));
        assertFalse(uut.isValidOrEmpty(null, "value name"));
    }

    @Test
    public void testValidInstant() {
        assertTrue(uut.isValidInstant(RAW_INSTANT, "name"));
    }

    @Test
    public void testInvalidInstant() {
        assertFalse(uut.isValidInstant("jfidfd", "name"));
    }

    @Test
    public void parsingMissingInstantGivesEpochDefault() {
        assertEquals(Instant.EPOCH, uut.parseToInstant(null, "name").get());
        assertEquals(Instant.EPOCH, uut.parseToInstant("", "name").get());
    }

    @Test
    public void validInstantIsParsedSuccessfully() {
        assertTrue(uut.parseToInstant(RAW_INSTANT, "name").isPresent());
    }

    @Test
    public void invalidInstantGivesEmptyOptional() {
        assertFalse(uut.parseToInstant("invalid", "name").isPresent());
    }
}
