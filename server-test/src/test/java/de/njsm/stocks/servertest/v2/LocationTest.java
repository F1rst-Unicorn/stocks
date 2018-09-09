package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class LocationTest {

    @Test
    public void addAnItem() {
        addLocationType("Location5");

        assertOnLocation()
                .body("status", equalTo(0))
                .body("data.name", hasItem("Location5"));
    }

    @Test
    public void renameLocation() {
        String name = "Location4";
        String newName = "Location2";
        int id = createNewLocationType(name);

        assertOnRename(id, 0, newName)
                .body("status", equalTo(0));

        assertOnLocation()
                .body("data.name", hasItem(newName));
    }

    @Test
    public void renamingFailsWithWrongVersion() {
        String name = "Location3";
        String newName = "Location2";
        int id = createNewLocationType(name);

        assertOnRename(id, 99, newName)
                .body("status", equalTo(3));
    }

    @Test
    public void renamingUnknownIdIsReported() {
        String newName = "Location2";

        assertOnRename(-1, 0, newName)
                .body("status", equalTo(2));
    }

    @Test
    public void deleteLocation() {
        String name = "Location1";
        int id = createNewLocationType(name);

        assertOnDelete(id, 0)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingFailsWithWrongVersion() {
        String name = "Location1";
        int id = createNewLocationType(name);

        assertOnDelete(id, 99)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(-1, 0)
                .body("status", equalTo(2));
    }

    private ValidatableResponse assertOnDelete(int id, int version) {
        return
        given()
                .queryParam("id", id)
                .queryParam("version", version).
        when()
                .delete(TestSuite.DOMAIN + "/v2/location").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private int createNewLocationType(String name) {
        addLocationType(name);
        return getIdOfLocation(name);
    }

    private void addLocationType(String name) {
        given()
                .queryParam("name", name).
        when()
                .put(TestSuite.DOMAIN + "/v2/location").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private int getIdOfLocation(String name) {
        return assertOnLocation()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");

    }

    private ValidatableResponse assertOnRename(int id, int version, String newName) {
        return
        given()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("new", newName).
        when()
                .put(TestSuite.DOMAIN + "/v2/location/rename").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private ValidatableResponse assertOnLocation() {
        return
        when()
                .get(TestSuite.DOMAIN + "/v2/location").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
