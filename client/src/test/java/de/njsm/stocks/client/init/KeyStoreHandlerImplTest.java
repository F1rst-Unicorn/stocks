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
