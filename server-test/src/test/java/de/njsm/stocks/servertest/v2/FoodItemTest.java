package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;

public class FoodItemTest {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss.SSS-Z")
            .withZone(ZoneId.of("UTC"));

    @Test
    public void addFoodItem() {
        int locationId = LocationTest.createNewLocationType("food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);

        addFoodItem(date, locationId, foodId);

        assertOnItems()
                .body("data.eatByDate", hasItem(FORMAT.format(date)));
    }

    @Test
    public void editItem() {
        int locationId = LocationTest.createNewLocationType("food item");
        int movedLocation = LocationTest.createNewLocationType("moved food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId, foodId);

        assertOnEdit(id, 0, editedDate, movedLocation)
                .body("status", equalTo(0));

        int version = assertOnItems()
                .body("data.storedIn", hasItem(movedLocation))
                .body("data.eatByDate", hasItem(FORMAT.format(editedDate)))
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.version == 1 }.last().version");

        assertEquals(1, version);
    }

    @Test
    public void editInvalidVersionIsReported() {
        int locationId = LocationTest.createNewLocationType("food item");
        int movedLocation = LocationTest.createNewLocationType("moved food item");
        int foodId = FoodTest.createNewFoodType("food item");
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId, foodId);

        assertOnEdit(id, 99, editedDate, movedLocation)
                .body("status", equalTo(3));
    }

    @Test
    public void editInvalidIdIsReported() {
        Instant editedDate = Instant.ofEpochMilli(15);

        assertOnEdit(99999, 0, editedDate, 1)
                .body("status", equalTo(3));
    }

    @Test
    public void deleteItem() {
        int locationId = LocationTest.createNewLocationType("food item delete");
        int foodId = FoodTest.createNewFoodType("food item delete");
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId, foodId);

        assertOnDelete(id, 0)
                .body("status", equalTo(0));
    }

    @Test
    public void deletingInvalidVersionIsReported() {
        int locationId = LocationTest.createNewLocationType("food item delete");
        int foodId = FoodTest.createNewFoodType("food item delete");
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId, foodId);

        assertOnDelete(id, 99)
                .body("status", equalTo(3));
    }

    @Test
    public void deletingUnknownIdIsReported() {
        assertOnDelete(999, 0)
                .body("status", equalTo(2));
    }

    private ValidatableResponse assertOnDelete(int id, int version) {
        return given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version).
        when()
                .delete(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private static int createNewItem(Instant eatByDate, int storedIn, int ofType) {
        addFoodItem(eatByDate, storedIn, ofType);
        return getIdOfItem(eatByDate);
    }

    private static void addFoodItem(Instant eatByDate, int storedIn, int ofType) {
        given()
                .log().ifValidationFails()
                .queryParam("eatByDate", FORMAT.format(eatByDate))
                .queryParam("storedIn", storedIn)
                .queryParam("ofType", ofType).
        when()
                .put(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOfItem(Instant date) {
        return assertOnItems()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.eatBy == '" + FORMAT.format(date) + "' }.last().id");
    }

    private static ValidatableResponse assertOnEdit(int id, int version, Instant eatByDate, int storedIn) {
        return given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("eatByDate", FORMAT.format(eatByDate))
                .queryParam("storedIn", storedIn).
        when()
                .put(TestSuite.DOMAIN + "/v2/fooditem/edit").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private static ValidatableResponse assertOnItems() {
        return given()
                .log().ifValidationFails().
        when()
                .get(TestSuite.DOMAIN + "/v2/fooditem").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }
}
