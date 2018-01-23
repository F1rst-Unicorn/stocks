package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class X509CertificateAdminTest {

    @Test
    @Ignore("will be enabled as soon as /usr paths are no longer hard-coded")
    public void testParseCsr() throws IOException {
        X509CertificateAdmin uut = new X509CertificateAdmin();
        Principals p = uut.getPrincipals(1);
        Assert.assertEquals("Jack", p.getUsername());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals("Device", p.getDeviceName());
        Assert.assertEquals(1, p.getDid());
    }
}