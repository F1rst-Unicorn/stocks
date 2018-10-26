package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

public class UserTest {

    @Test
    public void addUser() {
        String name = "testuser";
        assertOnAdd(name)
                .body("status", equalTo(0));
        assertOnUsers()
                .body("data.name", hasItem(name));

    }

    @Test
    public void addInvalidName() {
        assertOnAdd("")
                .body("status", equalTo(7));
    }

    @Test
    public void deleteInvalidVersion() {
        assertOnDelete(1, -1)
                .body("status", equalTo(7));
    }

    @Test
    public void deleteInvalidId() {
        assertOnDelete(0, 1)
                .body("status", equalTo(7));
    }

    @Test
    public void deleteUser() {
        String name = "deleteusertest";
        assertOnAdd(name)
                .body("status", equalTo(0));

        int userId = assertOnUsers()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");

        assertOnDelete(userId, 0)
                .body("status", equalTo(0));
    }

    public static int createNewUser(String name) {
        assertOnAdd(name);
        return assertOnUsers()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");
    }

    public static ValidatableResponse assertOnDelete(int id, int version) {
        return given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version).
        when()
                .delete(TestSuite.DOMAIN + "/v2/user").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    public static ValidatableResponse assertOnAdd(String name) {
        return given()
                .log().ifValidationFails()
                .queryParam("name", name).
        when()
                .put(TestSuite.DOMAIN + "/v2/user").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    public static ValidatableResponse assertOnUsers() {
        return given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/user").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }
}
