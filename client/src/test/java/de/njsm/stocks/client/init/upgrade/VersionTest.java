package de.njsm.stocks.client.init.upgrade;

import org.junit.Test;

import static de.njsm.stocks.client.init.upgrade.Version.CURRENT;
import static de.njsm.stocks.client.init.upgrade.Version.PRE_VERSIONED;
import static de.njsm.stocks.client.init.upgrade.Version.V_0_5_0;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class VersionTest {

    @Test
    public void equalsWorks() throws Exception {
        assertEquals(V_0_5_0, V_0_5_0);
        assertEquals(PRE_VERSIONED, PRE_VERSIONED);
        assertEquals(CURRENT, CURRENT);
        assertNotEquals(V_0_5_0, PRE_VERSIONED);
        assertNotEquals(CURRENT, PRE_VERSIONED);
    }

    @Test
    public void comparingWorks() throws Exception {
        assertEquals(0, V_0_5_0.compareTo(V_0_5_0));
        assertEquals(0, V_0_5_0.compareTo(new Version(0,5,0)));
        assertEquals(-1, PRE_VERSIONED.compareTo(V_0_5_0));
        assertEquals(1, V_0_5_0.compareTo(PRE_VERSIONED));
    }
}