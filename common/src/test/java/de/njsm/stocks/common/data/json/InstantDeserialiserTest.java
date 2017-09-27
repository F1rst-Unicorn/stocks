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