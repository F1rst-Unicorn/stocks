package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class DeviceTest {

    @Test
    public void addADevice() {
        String name = "Mobile";
        int userId = UserTest.createNewUser("Jabba");

        addDevice(name, userId);

        assertOnDevices()
                .body("name", hasItems(name))
                .body("userId", hasItems(userId));
    }

    @Test
    public void removeADevice() {
        String name = "Mobile2";
        int userId = UserTest.createNewUser("Jabba");
        addDevice(name, userId);
        int id = getIdOfDevice(name);

        given()
                .contentType(ContentType.JSON)
                .body(new UserDevice(id, "", userId)).
        when()
                .put(TestSuite.DOMAIN + "/device/remove").
        then()
                .log().ifValidationFails()
                .statusCode(204);

        assertOnDevices()
                .body("name", not(hasItems(name)))
                .body("id", not(hasItems(id)));
    }

    public static Ticket createNewDevice(String name, int userId) {
        String ticket = addDevice(name, userId);
        int id = getIdOfDevice(name);

        return new Ticket(id, ticket, "");
    }

    private static int getIdOfDevice(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/device").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.name == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnDevices() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/device").
                then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static String addDevice(String firstName, int userId) {
        UserDevice data = new UserDevice(0, firstName, userId);
        return given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/device").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("pemFile", isEmptyOrNullString())
                .body("ticket", notNullValue())
                .extract()
                .jsonPath()
                .getString("ticket");
    }
}
