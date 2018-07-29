package de.njsm.stocks.servertest;

import org.junit.Test;

import static io.restassured.RestAssured.when;

public class InvalidAccessTest {

    @Test
    public void cannotAccessServerViaSentry() {
        when().
                get("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/location").
        then().
                statusCode(404);
    }

    @Test
    public void cannotAccessSentryViaServer() {
        when().
                post(TestSuite.DOMAIN + "/uac/newuser").
        then().
                statusCode(404);
    }
}
