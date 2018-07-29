package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.Location;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
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
                .contentType(ContentType.JSON)
                .body(new Location(1, "Cupboard")).
        when()
                .put(TestSuite.DOMAIN + "/location").
        then()
                .statusCode(204);
        given()
                .contentType(ContentType.JSON)
                .body(new Location(1, "Cupboard")).
        when()
                .put(TestSuite.DOMAIN + "/location/remove").
        then()
                .statusCode(204);
    }

    private String getLocationChangeDate() {
        Response response =
        when()
                .get(TestSuite.DOMAIN + "/update").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", iterableWithSize(6))
                .extract()
                .response();

        return response.jsonPath().getString("[0].lastUpdate");
    }
}
