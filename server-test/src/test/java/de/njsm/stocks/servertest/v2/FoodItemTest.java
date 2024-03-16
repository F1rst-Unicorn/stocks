/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.client.business.entities.FoodItemForSynchronisation;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v2.repo.FoodRepository;
import de.njsm.stocks.servertest.v2.repo.LocationRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Order(1200)
public class FoodItemTest extends Base implements Deleter {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm:ss.SSSSSS-Z")
            .withZone(ZoneId.of("UTC"));

    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        dagger.inject(this);
    }

    @Test
    void addFoodItem() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("addFoodItem");
        int foodId = FoodRepository.getAnyFoodId();
        Instant date = Instant.ofEpochMilli(14);

        addFoodItem(date, locationId.id(), foodId);

        var foodItems = updateService.getFoodItems(Instant.EPOCH);
        assertThat(foodItems).filteredOn(FoodItemForSynchronisation::storedIn, locationId.id())
                .isNotEmpty()
                .allMatch(v -> v.eatBy().equals(date))
                .allMatch(v -> v.registers() == 1)
                .allMatch(v -> v.buys() == 1)
                .allMatch(v -> v.ofType() == foodId);
    }

    @Test
    void editItem() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("editItem first");
        IdImpl<Location> movedLocation = locationRepository.createNewLocationType("editItem second");
        int foodId = FoodRepository.getAnyFoodId();
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId.id(), foodId);

        assertOnEdit(id, 0, editedDate, movedLocation.id())
                .statusCode(200)
                .body("status", equalTo(0));

        int version = assertOnItems()
                .body("data.storedIn", hasItem(movedLocation.id()))
                .body("data.eatByDate", hasItem(FORMAT.format(editedDate)))
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.version == 1 }.last().version");

        assertEquals(1, version);
    }

    @Test
    void editInvalidVersionIsReported() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("editInvalidVersionIsReported first");
        IdImpl<Location> movedLocation = locationRepository.createNewLocationType("editInvalidVersionIsReported second");
        int foodId = FoodRepository.getAnyFoodId();
        Instant date = Instant.ofEpochMilli(14);
        Instant editedDate = Instant.ofEpochMilli(15);
        int id = createNewItem(date, locationId.id(), foodId);

        assertOnEdit(id, 99, editedDate, movedLocation.id())
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    void editInvalidIdIsReported() {
        Instant editedDate = Instant.ofEpochMilli(15);

        assertOnEdit(99999, 0, editedDate, 1)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    void deleteItem() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("deleteItem");
        int foodId = FoodRepository.getAnyFoodId();
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId.id(), foodId);

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    void deletingInvalidVersionIsReported() {
        IdImpl<Location> locationId = locationRepository.createNewLocationType("food item delete");
        int foodId = FoodRepository.getAnyFoodId();
        Instant date = Instant.ofEpochMilli(14);
        int id = createNewItem(date, locationId.id(), foodId);

        assertOnDelete(id, 99)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    void deletingUnknownIdIsReported() {
        assertOnDelete(999, 0)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    static int createNewItem(Instant eatByDate, int storedIn, int ofType) {
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
                .getInt("data.findAll{ it.eatByDate == '" + FORMAT.format(date) + "' }.last().id");
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

    @Override
    public String getEndpoint() {
        return "/v2/fooditem";
    }

    @Inject
    void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
}
