package de.njsm.stocks.sentry.auth;

import de.njsm.stocks.sentry.data.Principals;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CertificateManagerTest {


    @Test
    public void testParseCorrectName() {

        CertificateManager uut = new CertificateManager();

        int uid = 3;
        int did = 6;
        String[] testInput = new String[] {
                "my_username",
                String.valueOf(uid),
                "my_device_name",
                String.valueOf(did)};
        String input = "CN=";
        for (int i = 0; i < testInput.length-1; i++){
            input = input.concat(testInput[i] + "$");
        }
        input = input.concat(testInput[testInput.length-1]);

        Principals p = uut.parseSubjectName(input);

        assertEquals(testInput[0], p.getUsername());
        assertEquals(uid, p.getUid());
        assertEquals(testInput[2], p.getDeviceName());
        assertEquals(did, p.getDid());

    }

    @Test(expected = SecurityException.class)
    public void testParseNameWithDollar() {
        CertificateManager uut = new CertificateManager();

        String[] testInput = new String[] {"my_user$name", "3",
                "my_device_name", "6"};
        String input = "CN=";
        for (int i = 0; i < testInput.length-1; i++){
            input = input.concat(testInput[i] + "$");
        }
        input = input.concat(testInput[testInput.length-1]);

        Principals p = uut.parseSubjectName(input);
    }

    @Test(expected = SecurityException.class)
    public void testTooFewDollars() {
        CertificateManager uut = new CertificateManager();

        Principals p = uut.parseSubjectName("CN=username$devicename$4");
    }

    @Test
    public void testParseCsr() {
        CertificateManager uut = new CertificateManager();

        try {
            Principals p = uut.getPrincipals("/usr/share/stocks/root/CA/intermediate/csr/user_1.csr.pem");
            p.getDeviceName();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
