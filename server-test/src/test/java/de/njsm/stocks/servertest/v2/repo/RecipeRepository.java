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

package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RecipeRepository {

    public static List<RecipeForGetting> getAll() {
        return given()
                .log().ifValidationFails()
                .when()
                .get(TestSuite.DOMAIN + "/v2/recipe")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .extract().as(new TypeRef<ListResponse<RecipeForGetting>>() {
                })
                .getData();
    }

    public static RecipeForGetting getRecipeWith(String name) {
        return getAll()
                .stream().filter(v -> v.name().equals(name))
                .findAny()
                .get();
    }

    public static RecipeForEditing.Builder loadRecipeForEditingWith(String name) {
        RecipeForGetting recipeForGetting = getRecipeWith(name);

        return RecipeForEditing.builder()
                .id(recipeForGetting.id())
                .version(recipeForGetting.version());
    }

    public static RecipeForDeletion loadRecipeForDeletionWith(String name) {
        RecipeForGetting recipeForGetting = getRecipeWith(name);

        return RecipeForDeletion.builder()
                .id(recipeForGetting.id())
                .version(recipeForGetting.version())
                .build();
    }

    public static FullRecipeForDeletion buildDeletionObject(Versionable<Recipe> recipe,
                                                            List<? extends Versionable<RecipeIngredient>> ingredients,
                                                            List<? extends Versionable<RecipeProduct>> products) {
        return FullRecipeForDeletion.builder()
                .recipe(RecipeForDeletion.builder()
                        .id(recipe.id())
                        .version(recipe.version())
                        .build())
                .ingredients(
                        ingredients.stream()
                                .map(v -> RecipeIngredientForDeletion.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .products(
                        products.stream()
                                .map(v -> RecipeProductForDeletion.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public static FullRecipeForEditing buildEditingObject(RecipeForEditing recipeForEditing,
                                                          List<RecipeIngredientForGetting> ingredients,
                                                          List<RecipeProductForGetting> products) {
        return FullRecipeForEditing.builder()
                .recipe(recipeForEditing)
                .ingredients(
                        ingredients.stream()
                                .map(v -> RecipeIngredientForEditing.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .amount(v.amount())
                                        .ingredient(v.ingredient())
                                        .recipe(v.recipe())
                                        .unit(v.unit())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .products(
                        products.stream()
                                .map(v -> RecipeProductForEditing.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .amount(v.amount())
                                        .product(v.product())
                                        .recipe(v.recipe())
                                        .unit(v.unit())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
