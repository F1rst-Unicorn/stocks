package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v1.UserTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class DeviceTest {

    private static int userId;

    @BeforeClass
    public static void getUser() {
        userId = UserTest.createNewUser("Jeannie");
    }

    @Test
    public void addDevice() {
        String name = "devicetestdevice";

        assertOnAdd(name, userId)
                .body("status", equalTo(0));
        
        assertOnDevices()
                .body("data.name", hasItems(name));
    }

    @Test
    public void deleteDevice() {
        String name = "devicedeletetest";

        int deviceId = assertOnAdd(name, userId)
                .extract()
                .jsonPath()
                .getInt("data.deviceId");

        given()
                .log().ifValidationFails()
                .queryParam("id", deviceId)
                .queryParam("version", 0).
        when()
                .delete(TestSuite.DOMAIN +  "/v2/device").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));

        assertOnDevices()
                .body("data.id", not(hasItems(deviceId)))
                .body("data.name", not(hasItems(name)));
    }

    @Test
    public void revokeDevice() {
        String name = "devicedeletetest";

        int deviceId = assertOnAdd(name, userId)
                .extract()
                .jsonPath()
                .getInt("data.deviceId");

        given()
                .log().ifValidationFails()
                .queryParam("id", deviceId)
                .queryParam("version", 0).
        when()
                .delete(TestSuite.DOMAIN +  "/v2/device/revoke").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));

        assertOnDevices()
                .body("data.id", hasItems(deviceId));
    }

    private ValidatableResponse assertOnAdd(String name, int userId) {
        return given()
                .log().ifValidationFails()
                .queryParam("name", name)
                .queryParam("belongsTo", userId).
        when()
                .put(TestSuite.DOMAIN + "/v2/device").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private ValidatableResponse assertOnDevices() {
        return given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/device").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }
}
