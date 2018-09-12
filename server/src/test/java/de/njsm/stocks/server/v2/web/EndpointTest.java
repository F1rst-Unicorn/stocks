package de.njsm.stocks.server.v2.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EndpointTest {

    private Endpoint uut;

    @Before
    public void setup() {
        uut = new Endpoint();
    }

    @Test
    public void positiveIdsAreValid() {
        for (int i = 1; i < 100; i++) {
            assertTrue(i + " is considered invalid", uut.isValid(i, "value name"));
        }
    }

    @Test
    public void negativeIdsAreInvalid() {
        for (int i = -100; i < 0; i++) {
            assertFalse(i + " is considered invalid", uut.isValid(i, "value name"));
        }
    }

    @Test
    public void zeroIsInvalidId() {
        assertFalse(uut.isValid(0, "value name"));
    }

    @Test
    public void positiveVersionsAreValid() {
        for (int i = 1; i < 100; i++) {
            assertTrue(i + " is considered invalid", uut.isValid(i, "value name"));
        }
    }

    @Test
    public void negativeVersionsAreInvalid() {
        for (int i = -100; i < 0; i++) {
            assertFalse(i + " is considered invalid", uut.isValid(i, "value name"));
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
}