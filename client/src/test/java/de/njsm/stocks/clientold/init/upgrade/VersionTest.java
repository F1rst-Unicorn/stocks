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

package de.njsm.stocks.clientold.init.upgrade;

import org.junit.Test;

import static de.njsm.stocks.clientold.init.upgrade.Version.CURRENT;
import static de.njsm.stocks.clientold.init.upgrade.Version.PRE_VERSIONED;
import static de.njsm.stocks.clientold.init.upgrade.Version.V_0_5_0;
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

    @Test
    public void testValidParsing() throws Exception {
        assertEquals(V_0_5_0, Version.create("0.5.0"));
        assertEquals(new Version(1,2,4), Version.create("1.2.4"));
    }

    @Test
    public void invalidParsingGivesDefault() throws Exception {
        assertEquals(PRE_VERSIONED, Version.create("0.5-1"));
    }
}
