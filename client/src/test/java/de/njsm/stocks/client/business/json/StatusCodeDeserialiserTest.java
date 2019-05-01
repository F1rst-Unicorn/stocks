package de.njsm.stocks.client.business.json;

import de.njsm.stocks.client.business.StatusCode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
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