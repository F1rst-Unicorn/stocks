package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.EanNumber;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class EanTest {

    @Test
    public void addAEan() {
        String name = "1231231231231";
        int foodId = FoodTest.createNewFood("Banana");

        addEan(name, foodId);

        assertOnEans()
                .body("eanCode", hasItems(name));
    }

    @Test
    public void removeAEan() {
        String name = "1231231231232";
        int foodId = FoodTest.createNewFood("Banana");
        addEan(name, foodId);
        int id = getIdOfEan(name);

        given()
                .contentType(ContentType.JSON)
                .body(new EanNumber(id, "", 0)).
        when()
                .put(TestSuite.DOMAIN + "/ean/remove").
        then()
                .statusCode(204);

        assertOnEans()
                .body("eanCode", not(hasItems(name)));
    }

    private static int getIdOfEan(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/ean").
            then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.eanCode == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnEans() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/ean").
                then()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addEan(String firstName, int foodId) {
        EanNumber data = new EanNumber(0, firstName, foodId);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/ean").
        then()
                .statusCode(204);
    }
}
