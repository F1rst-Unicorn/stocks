package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RecipeProductRepository {

    public static List<RecipeProductForGetting> getOfRecipe(Identifiable<Recipe> recipe) {
        List<RecipeProductForGetting> products = given()
                .log().ifValidationFails()
                .when()
                .get(TestSuite.DOMAIN + "/v2/recipe-product")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .extract().as(new TypeRef<ListResponse<RecipeProductForGetting>>(){})
                .getData();

        products.removeIf(v -> v.recipe() != recipe.id());
        return products;
    }
}
