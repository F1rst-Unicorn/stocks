package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertTrue;

public class UpdateChangeTest {

    @Test
    public void updatesChangeOnDataChange() {
        String olderDate = getLocationChangeDate();
        changeLocations();
        String youngerDate = getLocationChangeDate();

        assertTrue(olderDate.compareTo(youngerDate) < 0);
    }

    private void changeLocations() {
        given()
                .queryParam("name", "update").
        when()
                .put(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private String getLocationChangeDate() {
        Response response =
        when()
                .get(TestSuite.DOMAIN + "/v2/update").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", iterableWithSize(6))
                .extract()
                .response();

        return response.jsonPath().getString("data[0].lastUpdate");
    }
}
