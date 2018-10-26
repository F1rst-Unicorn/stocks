package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class InitialServerTest {

    @Test
    public void foodIsEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", is(empty()));
    }

    @Test
    public void locationsAreEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", is(empty()));
    }

    @Test
    public void foodItemsAreEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", is(empty()));
    }


    @Test
    public void eansAreEmpty() {
        when().
                get(TestSuite.DOMAIN + "/v2/ean").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", is(empty()));
    }

    @Test
    public void initialUserIsOnly() {
        when()
                .get(TestSuite.DOMAIN + "/v2/user").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", iterableWithSize(1))
                .body("data[0].name", equalTo("Jack"))
                .body("data[0].id", equalTo(1));
    }

    @Test
    public void initialDeviceIsOnly() {
        when()
                .get(TestSuite.DOMAIN + "/v2/device").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", iterableWithSize(1))
                .body("data[0].name", equalTo("Device"))
                .body("data[0].userId", equalTo(1))
                .body("data[0].id", equalTo(1));
    }
}
