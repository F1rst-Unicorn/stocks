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
import de.njsm.stocks.servertest.data.RecipeIngredientForInsertion;
import de.njsm.stocks.servertest.data.RecipeProductForInsertion;
import de.njsm.stocks.servertest.v2.repo.FoodRepository;
import de.njsm.stocks.servertest.v2.repo.UnitRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class RecipeTest extends Base implements Deleter {

    @Test
    public void addingARecipeWorks() {
        String name = getUniqueName("addingARecipeWorks");
        RecipeForInsertion recipe = RecipeForInsertion.builder()
                .name(name)
                .instructions("instruction")
                .duration(Duration.ofHours(2))
                .build();

        FullRecipeForInsertion input = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .ingredients(Collections.emptyList())
                .products(Collections.emptyList())
                .build();

        put(input);

        assertOnGet()
                .body("data.name", hasItem(equalTo(name)));
    }

    @Test
    public void addingARecipeWithIngredientsAndProductsWorks() {
        int foodId = FoodRepository.getAnyFoodId();
        int unitId = UnitRepository.getAnyUnitId();

        String name = "addingARecipeWithIngredientsAndProductsWorks";
        RecipeForInsertion recipe = RecipeForInsertion.builder()
                .name(name)
                .instructions("instruction")
                .duration(Duration.ofHours(2))
                .build();

        RecipeIngredientForInsertion ingredient = RecipeIngredientForInsertion.builder()
                .ingredient(foodId)
                .amount(1)
                .unit(unitId)
                .build();

        RecipeProductForInsertion product = RecipeProductForInsertion.builder()
                .product(foodId)
                .amount(1)
                .unit(unitId)
                .build();

        FullRecipeForInsertion input = FullRecipeForInsertion.builder()
                .recipe(recipe)
                .addIngredient(ingredient)
                .addIngredient(ingredient)
                .addProduct(product)
                .build();

        put(input);

        assertOnGet()
                .body("data.name", hasItem(equalTo(name)));
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

    @Test
    public void deletingWithoutBodyIsRejected() {
        given()
                .log().ifValidationFails().
        when()
                .delete(TestSuite.DOMAIN + "/v2/recipe").
        then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    private void put(FullRecipeForInsertion input) {
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

    private ValidatableResponse assertOnGet() {
        return given()
                .log().ifValidationFails()
                .queryParam("bitemporal", 0)
        .when()
                .get(TestSuite.DOMAIN + getEndpoint())
        .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Override
    public String getEndpoint() {
        return "/v2/recipe";
    }
}
