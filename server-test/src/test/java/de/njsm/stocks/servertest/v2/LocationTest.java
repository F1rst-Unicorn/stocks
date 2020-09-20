/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.time.Instant;

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
                .body("data.name", hasItem("Location5"))
                .body("data.creatorUser", hasItem(1))
                .body("data.creatorUserDevice", hasItem(1));
    }

    @Test
    public void renameLocation() {
        String name = "Location4";
        String newName = "Location2";
        int id = createNewLocationType(name);

        assertOnRename(id, 0, newName)
                .statusCode(200)
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
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void renamingUnknownIdIsReported() {
        String newName = "Location2";

        assertOnRename(9999, 0, newName)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    public void deleteLocation() {
        String name = "Location1";
        int id = createNewLocationType(name);

        assertOnDelete(id, 0, false)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingFailsWithWrongVersion() {
        String name = "Location1";
        int id = createNewLocationType(name);

        assertOnDelete(id, 99, false)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(99999, 0, false)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    public void deleteCascadinglySucceeds() {
        int locId = createNewLocationType("cascadingTest");
        int foodId = FoodTest.createNewFoodType("cascadingTest");
        FoodItemTest.createNewItem(Instant.EPOCH, locId, foodId);

        assertOnDelete(locId, 0, false)
                .body("status", equalTo(4));

        assertOnDelete(locId, 0, true)
                .body("status", equalTo(0));
    }

    ValidatableResponse assertOnDelete(int id, int version, boolean cascade) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("cascade", cascade ? 1 : 0).
        when()
                .delete(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    static int createNewLocationType(String name) {
        addLocationType(name);
        return getIdOfLocation(name);
    }

    static void addLocationType(String name) {
        given()
                .log().ifValidationFails()
                .queryParam("name", name).
        when()
                .put(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    static int getIdOfLocation(String name) {
        return assertOnLocation()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");

    }

    private ValidatableResponse assertOnRename(int id, int version, String newName) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("new", newName).
        when()
                .put(TestSuite.DOMAIN + "/v2/location/rename").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    private static ValidatableResponse assertOnLocation() {
        return
        when()
                .get(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
