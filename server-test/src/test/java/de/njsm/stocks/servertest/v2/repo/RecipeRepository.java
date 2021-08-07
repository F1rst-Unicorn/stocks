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
}
