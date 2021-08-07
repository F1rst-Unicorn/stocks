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
