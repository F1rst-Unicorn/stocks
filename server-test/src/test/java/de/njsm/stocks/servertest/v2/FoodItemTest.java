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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;

public class FoodItemTest {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss.SSS-Z")
            .withZone(ZoneId.of("UTC"));

    @Test
    public void addFoodItem() {
        int locationId = LocationTest.createNewLocationType("food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);

        addFoodItem(date, locationId, foodId);

        assertOnItems()
                .body("data.ofType", hasItems(foodId))
                .body("data.storedIn", hasItems(locationId))
                .body("data.eatByDate", hasItem(FORMAT.format(date)))
                .body("data.registers", hasItems(1))
                .body("data.buys", hasItems(1));
    }

    @Test
    public void editItem() {
        int locationId = LocationTest.createNewLocationType("food item");
        int movedLocation = LocationTest.createNewLocationType("moved food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId, foodId);

        assertOnEdit(id, 0, editedDate, movedLocation)
                .statusCode(200)
                .body("status", equalTo(0));

        int version = assertOnItems()
                .body("data.storedIn", hasItem(movedLocation))
                .body("data.eatByDate", hasItem(FORMAT.format(editedDate)))
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.version == 1 }.last().version");

        assertEquals(1, version);
    }

    @Test
    public void editInvalidVersionIsReported() {
        int locationId = LocationTest.createNewLocationType("food item");
        int movedLocation = LocationTest.createNewLocationType("moved food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId, foodId);

        assertOnEdit(id, 99, editedDate, movedLocation)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void editInvalidIdIsReported() {
        Instant editedDate = Instant.ofEpochMilli(15);

        assertOnEdit(99999, 0, editedDate, 1)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    public void deleteItem() {
        int locationId = LocationTest.createNewLocationType("food item delete");
        int foodId = FoodTest.createNewFoodType("food item delete");
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId, foodId);

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingInvalidVersionIsReported() {
        int locationId = LocationTest.createNewLocationType("food item delete");
        int foodId = FoodTest.createNewFoodType("food item delete");
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId, foodId);

        assertOnDelete(id, 99)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(999, 0)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    private ValidatableResponse assertOnDelete(int id, int version) {
        return given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version).
        when()
                .delete(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    static int createNewItem(Instant eatByDate, int storedIn, int ofType) {
        addFoodItem(eatByDate, storedIn, ofType);
        return getIdOfItem(eatByDate);
    }

    private static void addFoodItem(Instant eatByDate, int storedIn, int ofType) {
        given()
                .log().ifValidationFails()
                .queryParam("eatByDate", FORMAT.format(eatByDate))
                .queryParam("storedIn", storedIn)
                .queryParam("ofType", ofType).
        when()
                .put(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOfItem(Instant date) {
        return assertOnItems()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.eatByDate == '" + FORMAT.format(date) + "' }.last().id");
    }

    private static ValidatableResponse assertOnEdit(int id, int version, Instant eatByDate, int storedIn) {
        return given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("eatByDate", FORMAT.format(eatByDate))
                .queryParam("storedIn", storedIn).
        when()
                .put(TestSuite.DOMAIN + "/v2/fooditem/edit").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    private static ValidatableResponse assertOnItems() {
        return given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }
}
