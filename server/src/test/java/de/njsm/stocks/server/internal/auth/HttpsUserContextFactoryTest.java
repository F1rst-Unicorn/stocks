package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.server.endpoints.BaseTestEndpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HttpsUserContextFactoryTest {

    @Test
    public void testFullCall() {
        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals output = uut.getPrincipals(BaseTestEndpoint.createMockRequest());

        assertEquals(BaseTestEndpoint.TEST_USER, output);
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
            Assert.assertTrue(HttpsUserContextFactory.isNameValid(input));
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
            Assert.assertFalse(HttpsUserContextFactory.isNameValid(input));
        }
    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        uut.parseSubjectName(input);

    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }
}
