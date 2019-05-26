/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.common.data;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrincipalsTest {

    @Test
    public void testSuccessful() {
        String[] input = {
                "user",
                "4",
                "device",
                "5"
        };

        Principals uut = new Principals(input);

        assertEquals("user", uut.getUsername());
        assertEquals(4, uut.getUid());
        assertEquals("device", uut.getDeviceName());
        assertEquals(5, uut.getDid());
    }

    @Test
    public void typeSafeConstructor() {
        int uid = 1;
        int did = 4;
        String user = "user";
        String device = "device";

        Principals uut = new Principals(user, device, uid, did);

        assertEquals(user, uut.getUsername());
        assertEquals(uid, uut.getUid());
        assertEquals(device, uut.getDeviceName());
        assertEquals(did, uut.getDid());
    }

    @Test(expected = SecurityException.class)
    public void invalidNumber() {
        String uname = "username";
        String dname = "device";
        int did = 6;

        String[] rawInput = new String[] {
                uname,
                "fakenumber",
                dname,
                String.valueOf(did)
        };

        new Principals(rawInput);
    }

    @Test(expected = SecurityException.class)
    public void invalidArray() {
        String uname = "username";
        String dname = "device";
        int uid = 4;
        int did = 6;

        String[] rawInput = new String[] {
                uname,
                String.valueOf(uid),
                dname,
                String.valueOf(did),
                "I'm evil :)"
        };

        new Principals(rawInput);
    }

    @Test(expected = SecurityException.class)
    public void testWrongLength() {
        String[] input = {
                "user",
                "4",
                "device",
        };

        new Principals(input);
    }


    @Test
    public void testEquality() {
        Principals uut1 = new Principals("test", "test", 1, 2);
        Principals uut2 = new Principals("test", "test", 1, 3);

        Assert.assertTrue(uut1.equals(uut1));
        Assert.assertTrue(uut2.equals(uut2));
        Assert.assertFalse(uut1.equals(new Object()));
        Assert.assertFalse(uut1.equals(uut2));
    }

    @Test
    public void hashsAreEqual() {
        Principals uut1 = new Principals("user", "device", 1, 2);
        Principals uut2 = new Principals("user", "device", 1, 2);

        assertEquals(uut1.hashCode(), uut2.hashCode());
    }
}
