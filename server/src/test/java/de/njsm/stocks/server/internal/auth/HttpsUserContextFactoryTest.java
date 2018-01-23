package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;
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
    public void testParseCorrectName() {

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

        Principals p = HttpsUserContextFactory.parseSubjectName(input);

        assertEquals(testInput[0], p.getUsername());
        assertEquals(uid, p.getUid());
        assertEquals(testInput[2], p.getDeviceName());
        assertEquals(did, p.getDid());

    }


    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        Principals p = HttpsUserContextFactory.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        Principals p = HttpsUserContextFactory.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testNameWithSpacesAndSpecialCharacters() {
        String input = "CN=John Doe$1$my-test_device$1";

        Principals p = HttpsUserContextFactory.parseSubjectName(input);

        assertEquals("John Doe", p.getUsername());
        assertEquals("my-test_device", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        HttpsUserContextFactory.parseSubjectName(input);

    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        Principals p = HttpsUserContextFactory.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testParseNameWithDollar() {
        String[] testInput = new String[] {"my_user$name", "3",
                "my_device_name", "6"};
        String input = "CN=";
        for (int i = 0; i < testInput.length-1; i++){
            input = input.concat(testInput[i] + "$");
        }
        input = input.concat(testInput[testInput.length-1]);

        HttpsUserContextFactory.parseSubjectName(input);
    }

    @Test(expected = SecurityException.class)
    public void testTooFewDollars() {
        HttpsUserContextFactory.parseSubjectName("CN=username$devicename$4");
    }
}
