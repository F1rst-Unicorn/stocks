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

package de.njsm.stocks.clientold.network;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TcpHostTest {

    @Test
    @Parameters(method = "getValidPorts")
    public void testValidParameters(int portToTest) {
        assertTrue("Port " + portToTest, TcpHost.isValidPort(portToTest));
    }

    public Object[] getValidPorts() {
        return new Object[] {
                new Object[] {1},
                new Object[] {2},
                new Object[] {3},
                new Object[] {22},
                new Object[] {80},
                new Object[] {443},
                new Object[] {10910},
                new Object[] {10911},
                new Object[] {10912}
        };
    }

    @Test
    @Parameters(method = "getInvalidPorts")
    public void testInvalidParameters(int portToTest) {
        assertFalse("Port " + portToTest, TcpHost.isValidPort(portToTest));
    }

    public Object[] getInvalidPorts() {
        return new Object[] {
                new Object[] {0},
                new Object[] {-1},
                new Object[] {-10},
                new Object[] {-100},
                new Object[] {Integer.MIN_VALUE},
                new Object[] {65536},
                new Object[] {65537},
                new Object[] {65538},
                new Object[] {Integer.MAX_VALUE}
        };
    }

    @Test
    public void testToString() {
        String host = "any.host.name";
        int port = 5;
        TcpHost uut = new TcpHost(host, port);

        assertEquals(host + ":" + port, uut.toString());
    }


}
