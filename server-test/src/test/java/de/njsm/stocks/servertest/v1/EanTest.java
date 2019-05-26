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

package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.EanNumber;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class EanTest {

    @Test
    public void addAEan() {
        String name = "1231231231231";
        int foodId = FoodTest.createNewFood("Banana");

        addEan(name, foodId);

        assertOnEans()
                .body("eanCode", hasItems(name));
    }

    @Test
    public void removeAEan() {
        String name = "1231231231232";
        int foodId = FoodTest.createNewFood("Banana");
        addEan(name, foodId);
        int id = getIdOfEan(name);

        given()
                .contentType(ContentType.JSON)
                .body(new EanNumber(id, "", 0)).
        when()
                .put(TestSuite.DOMAIN + "/ean/remove").
        then()
                .log().ifValidationFails()
                .statusCode(204);

        assertOnEans()
                .body("eanCode", not(hasItems(name)));
    }

    private static int getIdOfEan(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/ean").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.eanCode == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnEans() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/ean").
                then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addEan(String firstName, int foodId) {
        EanNumber data = new EanNumber(0, firstName, foodId);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/ean").
        then()
                .log().ifValidationFails()
                .statusCode(204);
    }
}
