package de.njsm.stocks.server.v2.business.data;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class FoodTest {

    @Test
    public void testHashCode() {
        Food data = new Food(1, "Bread", 2);

        assertEquals(1997941322, data.hashCode());
    }

    @Test
    public void testToString() {
        Food data = new Food(1, "Bread", 2);

        assertEquals("Food (1, Bread, 2)", data.toString());
    }
}