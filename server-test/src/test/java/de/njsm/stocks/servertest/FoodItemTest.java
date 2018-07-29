package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.FoodItem;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.threeten.bp.Instant;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class FoodItemTest {

    @Test
    public void addAnItem() {
        int foodId = FoodTest.createNewFood("Bacon");
        int locationId = LocationTest.createNewLocation("Fridge");
        createNewItem(foodId, locationId);

        assertOnResult()
                .body("ofType", hasItems(foodId))
                .body("storedIn", hasItems(locationId))
                .body("eatByDate", hasItems("1970.01.01-00:00:00.000-+0000"))
                .body("registers", hasItems(1))
                .body("buys", hasItems(1));
    }

    @Test
    public void moveAnItem() {
        int foodId = FoodTest.createNewFood("Bacon");
        int locationId = LocationTest.createNewLocation("Fridge");
        int newLocationId = LocationTest.createNewLocation("Moved");
        int id = createNewItem(foodId, locationId);

        given()
                .contentType(ContentType.JSON)
                .body(new FoodItem(id, Instant.EPOCH, foodId, locationId, 0, 0)).
        when()
                .put(TestSuite.DOMAIN + "/food/fooditem/move/" + newLocationId).
        then()
                .statusCode(204);

        assertOnResult()
                .body("ofType", hasItems(foodId))
                .body("storedIn", hasItems(newLocationId))
                .body("eatByDate", hasItems("1970.01.01-00:00:00.000-+0000"))
                .body("registers", hasItems(1))
                .body("buys", hasItems(1));
    }

    @Test
    public void removeItem() {
        int foodId = FoodTest.createNewFood("Bacon");
        int locationId = LocationTest.createNewLocation("Fridge");
        int id = createNewItem(foodId, locationId);

        given()
                .contentType(ContentType.JSON)
                .body(new FoodItem(id, Instant.EPOCH, foodId, locationId, 0, 0)).
        when()
                .put(TestSuite.DOMAIN + "/food/fooditem/remove").
        then()
                .statusCode(204);

        assertOnResult()
                .body("id", not(hasItems(id)));
    }

    private ValidatableResponse assertOnResult() {
        return         when()
                .get(TestSuite.DOMAIN + "/food/fooditem").
                        then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private int createNewItem(int foodId, int locationId) {
        createItem(foodId, locationId);
        return getIdOfLastItem();
    }

    private void createItem(int foodId, int locationId) {
        given()
                .contentType(ContentType.JSON)
                .body(new FoodItem(0, Instant.EPOCH, foodId, locationId, 0, 0)).
        when()
                .put(TestSuite.DOMAIN + "/food/fooditem").
        then()
                .statusCode(204);
    }

    private int getIdOfLastItem() {
        return when()
                .get(TestSuite.DOMAIN + "/food/fooditem").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getInt("id.last()");
    }
}
