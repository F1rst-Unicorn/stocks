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

public class ScaledUnitTest implements Deleter {

    @Test
    public void addAnItem() {
        int id = createNew(42);

        assertOnData()
                .body("data.id", hasItem(id))
                .body("data.scale", hasItem(42));
    }

    @Test
    public void editAnItem() {
        int id = createNew(1409);

        assertOnEdit(id, 0, 1410)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    public void delete() {
        int id = createNew(43);

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingFailsWithWrongVersion() {
        int id = createNew(43);

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

    static int createNew(int scale) {
        int unit = UnitTest.createNew("scaled unit test", "scaled unit test");
        add(scale, unit);
        return getIdOf(scale);
    }

    private static void add(int scale, int unit) {
        given()
                .log().ifValidationFails()
                .queryParam("scale", scale)
                .queryParam("unit", unit).
        when()
                .put(TestSuite.DOMAIN + "/v2/scaled-unit").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOf(int scale) {
        return assertOnData()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.scale == " + scale + " }.last().id");
    }

    private static ValidatableResponse assertOnData() {
        return
        given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/scaled-unit").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private ValidatableResponse assertOnEdit(int id, int version, int scale) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("scale", scale)
                .queryParam("unit", 1).
        when()
                .put(TestSuite.DOMAIN + "/v2/scaled-unit/edit").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    @Override
    public String getEndpoint() {
        return "/v2/scaled-unit";
    }
}
