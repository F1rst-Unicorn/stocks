package de.njsm.stocks.server.v2.business.json;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InstantDeserialiserTest {

    private InstantDeserialiser uut;

    @Before
    public void setup() {
        uut = new InstantDeserialiser();
    }

    @Test
    public void invalidInputThrowsException() {
        try {
            uut.parseString("fdsaklföe");
            fail();
        } catch (DateTimeParseException e) {

        }
    }

    @Test
    public void validInputIsParsedWithoutModification() {
        String input = "1970.01.01-00:00:00.000-+0000";
        Instant expected = Instant.ofEpochMilli(0);

        Instant actual = uut.parseString(input);

        assertEquals(expected, actual);
    }
}