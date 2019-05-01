package de.njsm.stocks.client.business.json;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InstantDeserialiserTest {

    @Test
    public void constructorWorks() {
        new InstantDeserialiser();
    }

    @Test
    public void invalidInputThrowsException() {
        try {
            InstantDeserialiser.parseString("fdsaklföe");
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    public void emptyInputThrowsException() {
        try {
            InstantDeserialiser.parseString("");
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    public void nullInputThrowsException() {
        try {
            InstantDeserialiser.parseString(null);
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    public void validInputIsParsedWithoutModification() throws IOException {
        String input = "1970.01.01-00:00:00.000-+0000";
        Instant expected = Instant.ofEpochMilli(0);

        Instant actual = InstantDeserialiser.parseString(input);

        assertEquals(expected, actual);
    }
}