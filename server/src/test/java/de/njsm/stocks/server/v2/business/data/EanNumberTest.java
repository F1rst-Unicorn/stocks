package de.njsm.stocks.server.v2.business.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class EanNumberTest {

    EanNumber uut;

    @Before
    public void setup() {
        uut = new EanNumber(1, 2, "code", 3);
    }

    @Test
    public void testHashCode() {
        assertEquals(94865366, uut.hashCode());
    }

    @Test
    public void testEqualsFalse() {
        assertNotEquals(null, uut);
        assertNotEquals(new Object(), uut);
    }

    @Test
    public void testEqualsTrue() {
        assertEquals(uut, uut);
    }
}