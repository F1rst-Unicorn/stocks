package de.njsm.stocks.client.network;

import org.junit.Test;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

public class TcpHostTest {

    @Test
    public void testValidParameters() {
        for (int portToTest = 1; portToTest < 65536; portToTest++) {
            assertTrue("Port " + portToTest, TcpHost.isValidPort(portToTest));
        }
    }

    @Test
    public void testInvalidParameters() {
        assertFalse("Port 0", TcpHost.isValidPort(0));
        for (int portToTest = 65536; portToTest < 100000; portToTest++) {
            assertFalse("Port " + portToTest, TcpHost.isValidPort(portToTest));
        }
    }

    @Test
    public void testToString() {
        String host = "any.host.name";
        int port = 5;
        TcpHost uut = new TcpHost(host, port);

        assertEquals(host + ":" + port, uut.toString());
    }


}