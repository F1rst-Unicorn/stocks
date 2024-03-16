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
import de.njsm.stocks.servertest.v2.repo.FoodRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@Order(1100)
public class EanTest implements Deleter {

    @Test
    void addAEan() {
        String code = "1231231231231";
        int foodId = FoodRepository.getAnyFoodId();

        addEan(code, foodId);

        assertOnEans()
                .body("data.eanCode", hasItems(code))
                .body("data.identifiesFood", hasItems(foodId));
    }

    @Test
    void removeAEan() {
        String name = "1231231231232";
        int foodId = FoodRepository.getAnyFoodId();
        addEan(name, foodId);
        int id = getIdOfEan(name);

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));

        assertOnEans()
                .body("eanCode", not(hasItems(name)));
    }

    private static int getIdOfEan(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/v2/ean").
            then()
                    .log().ifValidationFails()
                     .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("status", equalTo(0))
                    .extract()
                    .jsonPath()
                    .getInt("data.findAll{ it.eanCode == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnEans() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/v2/ean").
                then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("status", equalTo(0));
    }

    private static void addEan(String firstName, int foodId) {
        given()
                .queryParam("code", firstName)
                .queryParam("identifies", foodId).
        when()
                .put(TestSuite.DOMAIN + "/v2/ean").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    @Override
    public String getEndpoint() {
        return "/v2/ean";
    }
}
