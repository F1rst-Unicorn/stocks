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

public class UnitTest implements Deleter {

    @Test
    public void addAnItem() {
        add("Liter", "l");

        assertOnData()
                .body("data.name", hasItem("Liter"))
                .body("data.abbreviation", hasItem("l"));
    }

    @Test
    public void rename() {
        String newName = "Cabal";
        String newAbbreviation = "fdsa";
        int id = createNew("Gramm", "g");

        assertOnRename(id, 0, newName, newAbbreviation)
                .statusCode(200)
                .body("status", equalTo(0));

        assertOnData()
                .body("data.name", hasItem(newName))
                .body("data.abbreviation", hasItem(newAbbreviation));
    }

    @Test
    public void renamingFailsWithWrongVersion() {
        String newName = "Cabal";
        String newAbbreviation = "fdsa";
        int id = createNew("Gramm", "g");

        assertOnRename(id, 99, newName, newAbbreviation)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void renamingUnknownIdIsReported() {
        assertOnRename(9999, 0, "newName", "fdsa")
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    public void delete() {
        String name = "Cookie";
        int id = createNew(name, "fdsa");

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingFailsWithWrongVersion() {
        String name = "Cookie";
        int id = createNew(name, "fdsa");

        assertOnDelete(id, 99)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(9999, 0)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    public static int createNew(String name, String abbreviation) {
        add(name, abbreviation);
        return getIdOf(name);
    }

    private static void add(String name, String abbreviation) {
        given()
                .log().ifValidationFails()
                .queryParam("name", name)
                .queryParam("abbreviation", abbreviation).
        when()
                .put(TestSuite.DOMAIN + "/v2/unit").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOf(String name) {
        return assertOnData()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");

    }

    private ValidatableResponse assertOnRename(int id, int version, String newName, String newAbbreviation) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("name", newName)
                .queryParam("abbreviation", newAbbreviation).
        when()
                .put(TestSuite.DOMAIN + "/v2/unit/rename").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    public static ValidatableResponse assertOnData() {
        return
        given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/unit").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    @Override
    public String getEndpoint() {
        return "/v2/unit";
    }
}
