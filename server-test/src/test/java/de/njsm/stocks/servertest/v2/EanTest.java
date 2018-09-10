package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class EanTest {

    @Test
    public void addAEan() {
        String code = "1231231231231";
        int foodId = FoodTest.createNewFoodType("Banana");

        addEan(code, foodId);

        assertOnEans()
                .body("data.eanCode", hasItems(code))
                .body("data.identifies", hasItems(foodId));
    }

    @Test
    public void removeAEan() {
        String name = "1231231231232";
        int foodId = FoodTest.createNewFoodType("Banana");
        addEan(name, foodId);
        int id = getIdOfEan(name);

        given()
                .queryParam("id", id)
                .queryParam("version", 0).
        when()
                .delete(TestSuite.DOMAIN + "/v2/ean").
        then()
                .statusCode(204);

        assertOnEans()
                .body("eanCode", not(hasItems(name)));
    }

    private static int getIdOfEan(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/v2/ean").
            then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.eanCode == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnEans() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/v2/ean").
                then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addEan(String firstName, int foodId) {
        given()
                .queryParam("code", firstName)
                .queryParam("identifies", foodId).
        when()
                .put(TestSuite.DOMAIN + "/v2/ean").
        then()
                .statusCode(204)
                .body("status", equalTo(0));
    }
}
