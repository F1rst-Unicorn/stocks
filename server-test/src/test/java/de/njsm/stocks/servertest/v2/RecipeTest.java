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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v2.repo.*;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        String name = putRecipeWithIngredientAndProduct("addingARecipeWithIngredientsAndProductsWorks");

        assertOnGet()
                .body("data.name", hasItem(equalTo(name)));
    }

    @Test
    public void invalidAddingIsRejected() {
        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body("{\"recipe\":{\"name\":\"" + getUniqueName("invalidAddingIsRejected") + "\",\"duration\":\"PT2H\"}}")
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

    @Test
    public void editingWithoutBodyIsRejected() {
        given()
                .log().ifValidationFails().
        when()
                .put(TestSuite.DOMAIN + "/v2/recipe/edit").
        then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    @Test
    public void validEditingWorks() {
        String name = putRecipeWithIngredientAndProduct("validEditingWorks");

        RecipeForEditing recipeForEditing = RecipeRepository.loadRecipeForEditingWith(name)
                .name(getUniqueName("validEditingWorks.newName"))
                .instructions("new instructions")
                .duration(Duration.ofHours(2))
                .build();
        List<RecipeIngredientForGetting> ingredients = RecipeIngredientRepository.getOfRecipe(recipeForEditing);
        List<RecipeProductForGetting> products = RecipeProductRepository.getOfRecipe(recipeForEditing).stream()
                .map(v -> v.toBuilder()
                        .amount(v.amount() + 1)
                        .build())
                .collect(Collectors.toList());
        FullRecipeForEditing fullRecipeForDeletion = RecipeRepository.buildEditingObject(recipeForEditing, ingredients, products);

        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(fullRecipeForDeletion)
        .when()
                .put(TestSuite.DOMAIN + getEndpoint() + "/edit")
        .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    @Test
    public void validDeletionWorks() {
        String name = putRecipeWithIngredientAndProduct("validDeletionWorks");

        RecipeForDeletion recipeForDeletion = RecipeRepository.loadRecipeForDeletionWith(name);
        List<RecipeIngredientForGetting> ingredients = RecipeIngredientRepository.getOfRecipe(recipeForDeletion);
        List<RecipeProductForGetting> products = RecipeProductRepository.getOfRecipe(recipeForDeletion);
        FullRecipeForDeletion fullRecipeForDeletion = RecipeRepository.buildDeletionObject(recipeForDeletion, ingredients, products);

        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(fullRecipeForDeletion)
        .when()
                .delete(TestSuite.DOMAIN + getEndpoint())
        .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private String putRecipeWithIngredientAndProduct(String distinguisher) {
        int foodId = FoodRepository.getAnyFoodId();
        int unitId = UnitRepository.getAnyUnitId();

        String name = getUniqueName(distinguisher);
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
        return name;
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
