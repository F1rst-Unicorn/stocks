/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import groovy.lang.Tuple2;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.*;

@Order(1400)
public class DeviceTest {

    private static int userId;

    @BeforeAll
    static void getUser() {
        userId = UserTest.createNewUser("Jeannie");
    }

    @Test
    void addDevice() {
        String name = "devicetestdevice";

        assertOnAdd(name, userId)
                .body("status", equalTo(0));

        assertOnDevices()
                .body("data.name", hasItems(name))
                .body("data.userId", hasItems(userId));
    }

    @Test
    void deleteDevice() {
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
    void revokeDevice() {
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

    static Tuple2<Integer, String> createNewDevice(String name, int userId) {
        String ticket = addDevice(name, userId);
        int id = getIdOfDevice(name);

        return new Tuple2<>(id, ticket);
    }

    private static int getIdOfDevice(String name) {
        return assertOnDevices()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");
    }



    private static String addDevice(String name, int userId) {
        return assertOnAdd(name, userId)
                .body("data.pemFile", isEmptyOrNullString())
                .body("data.ticket", not(isEmptyOrNullString()))
                .extract()
                .jsonPath()
                .getString("data.ticket");
    }

    private static ValidatableResponse assertOnAdd(String name, int userId) {
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

    private static ValidatableResponse assertOnDevices() {
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
