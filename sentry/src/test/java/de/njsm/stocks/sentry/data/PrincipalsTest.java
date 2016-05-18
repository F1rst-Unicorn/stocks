package de.njsm.stocks.sentry.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrincipalsTest {

    @Test
    public void validCreation() {
        String uname = "username";
        String dname = "device";
        int uid = 4;
        int did = 6;

        String[] rawInput = new String[] {
                uname,
                String.valueOf(uid),
                dname,
                String.valueOf(did)
        };

        Principals uut = new Principals(uname, dname, uid, did);

        Principals uut2 = new Principals(rawInput);

        assertEquals(uname, uut.getUsername());
        assertEquals(dname, uut.getDeviceName());
        assertEquals(uid, uut.getUid());
        assertEquals(did, uut.getDid());

        assertEquals(uname, uut2.getUsername());
        assertEquals(dname, uut2.getDeviceName());
        assertEquals(uid, uut2.getUid());
        assertEquals(did, uut2.getDid());
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

        Principals uut = new Principals(rawInput);
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

        Principals uut = new Principals(rawInput);
    }
}
