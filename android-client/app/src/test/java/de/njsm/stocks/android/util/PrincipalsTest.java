package de.njsm.stocks.android.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

        assertEquals(uut1, uut1);
        assertEquals(uut2, uut2);
        assertNotEquals(uut1, new Object());
        assertNotEquals(uut1, uut2);
    }

    @Test
    public void hashsAreEqual() {
        Principals uut1 = new Principals("user", "device", 1, 2);
        Principals uut2 = new Principals("user", "device", 1, 2);

        assertEquals(uut1.hashCode(), uut2.hashCode());
    }

    @Test
    public void testValidNames() {
        List<String> inputList = new ArrayList<>();
        inputList.add("John");
        inputList.add("mike");
        inputList.add("fdsaiofpra");
        inputList.add("Henry8th");
        inputList.add("123flowerpower");
        inputList.add(">> Master <<");
        inputList.add("!\"\\^,.;:§%&/()?+*#'-_<>|");

        for (String input : inputList) {
            Assert.assertTrue(Principals.isNameValid(input));
        }
    }

    @Test
    public void testInvalidNames() {
        List<String> inputList = new ArrayList<>();
        inputList.add("Adversary$1");
        inputList.add("AdversaryDevice$1");
        inputList.add("CN=John");
        inputList.add("==> Fool <==");

        for (String input : inputList) {
            Assert.assertFalse(Principals.isNameValid(input));
        }
    }

}
