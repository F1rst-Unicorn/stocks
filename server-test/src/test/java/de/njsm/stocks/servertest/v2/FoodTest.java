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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class FoodTest {

    @Test
    public void addAnItem() {
        addFoodType("Carrot");

        assertOnFood()
                .body("data.name", hasItem("Carrot"));
    }

    @Test
    public void renameFood() {
        String name = "Cake";
        String newName = "Cabal";
        int id = createNewFoodType(name);

        assertOnRename(id, 0, newName)
                .body("status", equalTo(0));

        assertOnFood()
                .body("data.name", hasItem(newName));
    }

    @Test
    public void renamingFailsWithWrongVersion() {
        String name = "Cinnamon";
        String newName = "Cabal";
        int id = createNewFoodType(name);

        assertOnRename(id, 99, newName)
                .body("status", equalTo(3));
    }

    @Test
    public void renamingUnknownIdIsReported() {
        String newName = "Cabal";

        assertOnRename(9999, 0, newName)
                .body("status", equalTo(2));
    }

    @Test
    public void deleteFood() {
        String name = "Cookie";
        int id = createNewFoodType(name);

        assertOnDelete(id, 0)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingFailsWithWrongVersion() {
        String name = "Cookie";
        int id = createNewFoodType(name);

        assertOnDelete(id, 99)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(9999, 0)
                .body("status", equalTo(2));
    }

    private ValidatableResponse assertOnDelete(int id, int version) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version).
        when()
                .delete(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    static int createNewFoodType(String name) {
        addFoodType(name);
        return getIdOfFood(name);
    }

    private static void addFoodType(String name) {
        given()
                .log().ifValidationFails()
                .queryParam("name", name).
        when()
                .put(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOfFood(String name) {
        return assertOnFood()
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
                .put(TestSuite.DOMAIN + "/v2/food/rename").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private static ValidatableResponse assertOnFood() {
        return
        given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }
}
