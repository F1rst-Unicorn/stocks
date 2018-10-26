package de.njsm.stocks.server.v2.business.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationTest {

    @Test
    public void testHashCode() {
        Location data = new Location(1, "Fridge", 2);

        assertEquals(1064553117, data.hashCode());
    }

    @Test
    public void testToString() {
        Location data = new Location(1, "Fridge", 2);

        assertEquals("Location (1, Fridge, 2)", data.toString());
    }
}