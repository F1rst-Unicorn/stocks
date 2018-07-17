package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrincipalFilterTest {

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

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals(testInput[0], p.getUsername());
        assertEquals(uid, p.getUid());
        assertEquals(testInput[2], p.getDeviceName());
        assertEquals(did, p.getDid());

    }


    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testNameWithSpacesAndSpecialCharacters() {
        String input = "CN=John Doe$1$my-test_device$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("John Doe", p.getUsername());
        assertEquals("my-test_device", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        PrincipalFilter.parseSubjectName(input);

    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        Principals p = PrincipalFilter.parseSubjectName(input);

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

        PrincipalFilter.parseSubjectName(input);
    }

    @Test(expected = SecurityException.class)
    public void testTooFewDollars() {
        PrincipalFilter.parseSubjectName("CN=username$devicename$4");
    }

    @Test(expected = SecurityException.class)
    public void testCompleteGarbage() {
        PrincipalFilter.parseSubjectName("29A");
    }


}