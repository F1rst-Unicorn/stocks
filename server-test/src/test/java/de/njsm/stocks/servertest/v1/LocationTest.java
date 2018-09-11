package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class LocationTest {

    @Test
    public void addALocation() {
        String name = "Cupboard";

        addLocation(name);

        assertOnLocations()
                .body("name", hasItems(name));
    }

    @Test
    public void renameALocation() {
        String firstName = "Fridge";
        String secondName = "Basement";
        addLocation(firstName);
        int id = getIdOfLocation(firstName);

        given().
                contentType(ContentType.JSON)
                .body(new Location(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/location/" + secondName).
        then()
                .log().ifValidationFails()
                .statusCode(204);

        assertOnLocations()
                .body("name", hasItems(secondName))
                .body("name", not(hasItems(firstName)));
    }

    @Test
    public void removeALocation() {
        String name = "fdsa";
        addLocation(name);
        int id = getIdOfLocation(name);

        given()
                .contentType(ContentType.JSON)
                .body(new Location(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/location/remove").
        then()
                .log().ifValidationFails()
                .statusCode(204);

        assertOnLocations()
                .body("name", not(hasItems(name)));
    }

    public static int createNewLocation(String name) {
        addLocation(name);
        return getIdOfLocation(name);
    }

    private static int getIdOfLocation(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/location").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.name == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnLocations() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/location").
                then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addLocation(String firstName) {
        Location data = new Location(0, firstName);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/location").
        then()
                .log().ifValidationFails()
                .statusCode(204);
    }
}
