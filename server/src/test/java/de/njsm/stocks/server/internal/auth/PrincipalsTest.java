package de.njsm.stocks.server.internal.auth;

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
    public void testError() {
        String[] input = {
                "user",
                "wrong",
                "device",
                "5"
        };

        new Principals(input);

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
    public void hashsAreEqual() {
        Principals uut1 = new Principals("user", "device", 1, 2);
        Principals uut2 = new Principals("user", "device", 1, 2);

        assertEquals(uut1.hashCode(), uut2.hashCode());
    }
}
