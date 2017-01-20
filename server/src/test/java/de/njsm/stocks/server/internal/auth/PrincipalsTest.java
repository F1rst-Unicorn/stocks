package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.server.internal.auth.Principals;
import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertEquals("user", uut.getUsername());
        Assert.assertEquals(4, uut.getUid());
        Assert.assertEquals("device", uut.getDeviceName());
        Assert.assertEquals(5, uut.getDid());
    }

    @Test
    public void typeSafeConstructor() {
        int uid = 1;
        int did = 4;
        String user = "user";
        String device = "device";

        Principals uut = new Principals(user, device, uid, did);

        Assert.assertEquals(user, uut.getUsername());
        Assert.assertEquals(uid, uut.getUid());
        Assert.assertEquals(device, uut.getDeviceName());
        Assert.assertEquals(did, uut.getDid());
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
}
