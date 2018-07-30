package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class InitialServerTest {

    @Test
    public void foodIsEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/food").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", is(empty()));
    }

    @Test
    public void locationsAreEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/location").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", is(empty()));
    }

    @Test
    public void foodItemsAreEmpty() {
        when()
                .get(TestSuite.DOMAIN + "/food/fooditem").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", is(empty()));
    }


    @Test
    public void eansAreEmpty() {
        when().
                get(TestSuite.DOMAIN + "/ean").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", is(empty()));
    }

    @Test
    public void initialUserIsOnly() {
        when()
                .get(TestSuite.DOMAIN + "/user").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", iterableWithSize(1))
                .body("[0].name", equalTo("Jack"))
                .body("[0].id", equalTo(1));
    }

    @Test
    public void initialDeviceIsOnly() {
        when()
                .get(TestSuite.DOMAIN + "/device").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", iterableWithSize(1))
                .body("[0].name", equalTo("Device"))
                .body("[0].userId", equalTo(1))
                .body("[0].id", equalTo(1));
    }
}
