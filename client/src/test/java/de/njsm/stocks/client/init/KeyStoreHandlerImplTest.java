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

package de.njsm.stocks.client.init;

import de.njsm.stocks.client.exceptions.CryptoException;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class KeyStoreHandlerImplTest {

    private static KeyStoreHandlerImpl uut;

    @BeforeClass
    public static void setup() throws CryptoException {
        uut = new KeyStoreHandlerImpl();
        uut.startKeyGeneration();
    }

    @Test
    public void testKeyGeneration() throws CryptoException {

        uut.generateNewKey();

        assertNotNull(uut.getClientKeys().getPrivate());
        assertNotNull(uut.getClientKeys().getPublic());
    }

    @Test
    public void testCsrGeneration() throws CryptoException {
        uut.generateNewKey();

        String csr = uut.generateCsr("test user");

        assertTrue(csr.contains("-----BEGIN CERTIFICATE REQUEST-----"));
        assertTrue(csr.contains("-----END CERTIFICATE REQUEST-----"));
    }

    @Test
    public void testFingerprintReading() throws IOException, CryptoException {
        String pemFile = IOUtils.toString(ClassLoader.getSystemResourceAsStream("example-cert.crt.pem"));

        String fpr = uut.getFingerPrintFromPem(pemFile);

        assertEquals("B7:F7:94:CD:46:8E:4A:E6:50:74:C3:70:5D:4D:98:55:4A:11:88:46:3B:63:63:B7:F7:8A:D3:13:12:8B:F9:10",
                fpr);
    }
}
