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
import de.njsm.stocks.servertest.data.FullRecipeForInsertion;
import de.njsm.stocks.servertest.data.RecipeForInsertion;
import io.restassured.http.ContentType;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RecipeTest implements Deleter {

    @Test
    public void addingARecipeWorks() {
        RecipeForInsertion recipe = RecipeForInsertion.builder()
                .name("addingARecipeWorks")
                .instructions("instruction")
                .duration(Duration.ofHours(2))
                .build();

        FullRecipeForInsertion input = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(Collections.emptyList())
                .products(Collections.emptyList())
                .build();

        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(input)
        .when()
                .put(TestSuite.DOMAIN + getEndpoint())
        .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    @Test
    public void invalidAddingIsRejected() {
        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body("{\"recipe\":{\"name\":\"invalidAddingIsRejected\",\"instructions\":\"instruction\",\"duration\":\"PT2H\"}}")
        .when()
                .put(TestSuite.DOMAIN + getEndpoint())
        .then()
                .log().ifValidationFails()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("status", equalTo(7));
    }

    @Override
    public String getEndpoint() {
        return "/v2/recipe";
    }
}
