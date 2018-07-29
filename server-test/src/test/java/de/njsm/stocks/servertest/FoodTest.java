package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.Food;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class FoodTest {

    @Test
    public void addAFood() {
        String name = "Beer";

        addFood(name);

        assertOnFoods()
                .body("name", hasItems(name));
    }

    @Test
    public void renameAFood() {
        String firstName = "Banana";
        String secondName = "Bread";
        int id = createNewFood(firstName);

        given().
                contentType(ContentType.JSON)
                .body(new Food(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/food/" + secondName).
        then()
                .statusCode(204);

        assertOnFoods()
                .body("name", hasItems(secondName))
                .body("name", not(hasItems(firstName)));
    }

    @Test
    public void removeAFood() {
        String name = "Butter";
        addFood(name);
        int id = getIdOfFood(name);

        given()
                .contentType(ContentType.JSON)
                .body(new Food(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/food/remove").
        then()
                .statusCode(204);

        assertOnFoods()
                .body("name", not(hasItems(name)));
    }

    public static int createNewFood(String name) {
        addFood(name);
        return getIdOfFood(name);
    }

    private static int getIdOfFood(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/food").
            then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.name == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnFoods() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/food").
                then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addFood(String firstName) {
        Food data = new Food(0, firstName);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/food").
        then()
                .statusCode(204);
    }
}
