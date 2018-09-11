package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.servertest.TestSuite;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class InvalidAccessTest {

    @Test
    public void cannotAccessServerViaSentry() {
        when().
                get("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/location").
        then()
                .log().ifValidationFails()
                .statusCode(404);
    }

    @Test
    public void cannotAccessSentryViaServer() {
        when().
                post(TestSuite.DOMAIN + "/uac/newuser").
        then()
                .log().ifValidationFails()
                .statusCode(404);
    }
}
