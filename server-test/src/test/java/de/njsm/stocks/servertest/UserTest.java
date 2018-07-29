package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.User;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class UserTest {

    @Test
    public void addAUser() {
        String name = "John";

        addUser(name);

        assertOnUsers()
                .body("name", hasItems(name));
    }

    @Test
    public void removeAUser() {
        String name = "July";
        addUser(name);
        int id = getIdOfUser(name);

        given()
                .contentType(ContentType.JSON)
                .body(new User(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/user/remove").
        then()
                .statusCode(204);

        assertOnUsers()
                .body("name", not(hasItems(name)));
    }

    public static int createNewUser(String name) {
        addUser(name);
        return getIdOfUser(name);
    }

    private static int getIdOfUser(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/user").
            then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.name == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnUsers() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/user").
                then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addUser(String firstName) {
        User data = new User(0, firstName);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/user").
        then()
                .statusCode(204);
    }
}
