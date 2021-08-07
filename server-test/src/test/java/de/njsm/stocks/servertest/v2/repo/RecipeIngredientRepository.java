package de.njsm.stocks.servertest.v2.repo;

import de.njsm.stocks.common.api.Identifiable;
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.Recipe;
import de.njsm.stocks.common.api.RecipeIngredientForGetting;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RecipeIngredientRepository {

    public static List<RecipeIngredientForGetting> getOfRecipe(Identifiable<Recipe> recipe) {
        List<RecipeIngredientForGetting> ingredients = given()
                .log().ifValidationFails()
                .when()
                .get(TestSuite.DOMAIN + "/v2/recipe-ingredient")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .extract().as(new TypeRef<ListResponse<RecipeIngredientForGetting>>(){})
                .getData();

        ingredients.removeIf(v -> v.recipe() != recipe.id());
        return ingredients;
    }
}
