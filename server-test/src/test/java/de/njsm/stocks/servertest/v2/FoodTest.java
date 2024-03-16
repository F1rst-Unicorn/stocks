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

import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v2.repo.LocationRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Order(800)
public class FoodTest extends Base implements Deleter {

    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        dagger.inject(this);
    }

    @Test
    void testBitemporalFood() {
        addFoodType(getUniqueName("testBitemporalFood"));

        assertOnFood(true)
                .body("data.validTimeStart", not(emptyIterable()))
                .body("data.validTimeEnd", not(emptyIterable()))
                .body("data.transactionTimeStart", not(emptyIterable()))
                .body("data.transactionTimeEnd", not(emptyIterable()))
                .body("data.toBuy", hasItem(false));
    }

    @Test
    void addAnItem() {
        String name = getUniqueName("addAnItem");
        addFoodType(name);

        assertOnFood()
                .body("data.name", hasItem(name));
    }

    @Test
    void renameFood() {
        String name = getUniqueName("renameFood");
        String newName = name + ".new";
        int id = createNewFoodType(name);
        IdImpl<Location> locationId = locationRepository.createNewLocationType(name);

        assertOnRename(id, 0, newName, 42, locationId.id())
                .statusCode(200)
                .body("status", equalTo(0));

        assertOnFood()
                .body("data.name", hasItem(newName))
                .body("data.expirationOffset", hasItem(42))
                .body("data.location", hasItem(locationId.id()));
    }

    @Test
    void renameFoodWithDescription() {
        String name = getUniqueName("renameFoodWithDescription");
        String newName = name + ".new";
        int id = createNewFoodType(name);
        IdImpl<Location> locationId = locationRepository.createNewLocationType(name);
        String description = "description";

        assertOnEdit(id, 0, newName, 42, locationId.id(), description)
                .statusCode(200)
                .body("status", equalTo(0));

        assertOnFood()
                .body("data.name", hasItem(newName))
                .body("data.expirationOffset", hasItem(42))
                .body("data.description", hasItem(description))
                .body("data.location", hasItem(locationId.id()));
    }

    @Test
    void alterFoodDescription() {
        String name = getUniqueName("alterFoodDescription");
        String newDescription = "new description";
        int id = createNewFoodType(name);

        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", 0)
                .formParam("description", newDescription).
        when()
                .post(TestSuite.DOMAIN + "/v2/food/description").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("status", equalTo(0));

        assertOnFood()
                .body("data.description", hasItem(newDescription));
    }

    @Test
    void setBuyStatus() {
        String name = getUniqueName("setBuyStatus");
        int id = createNewFoodType(name);

        assertOnSetBuyStatus(id, 0, true)
                .body("status", equalTo(0));

        assertOnFood()
                .body("data.name", hasItem(name))
                .body("data.toBuy", hasItem(true));

    }

    @Test
    void renamingFailsWithWrongVersion() {
        String name = getUniqueName("renamingFailsWithWrongVersion");
        String newName = name + ".new";
        int id = createNewFoodType(name);

        assertOnRename(id, 99, newName, 0, 0)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    void renamingUnknownIdIsReported() {
        assertOnRename(9999, 0, "dummy", 0, 0)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    @Test
    void deleteFood() {
        String name = getUniqueName("deleteFood");
        int id = createNewFoodType(name);

        assertOnDelete(id, 0)
                .statusCode(200)
                .body("status", equalTo(0));
    }

    @Test
    void deletingFailsWithWrongVersion() {
        String name = getUniqueName("deletingFailsWithWrongVersion");
        int id = createNewFoodType(name);

        assertOnDelete(id, 99)
                .statusCode(400)
                .body("status", equalTo(3));
    }

    @Test
    void deletingUnknownIdIsReported() {
        assertOnDelete(9999, 0)
                .statusCode(404)
                .body("status", equalTo(2));
    }

    public static int createNewFoodType(String name) {
        addFoodType(name);
        return getIdOfFood(name);
    }

    private static void addFoodType(String name) {
        given()
                .log().ifValidationFails()
                .queryParam("name", name)
        .when()
                .put(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private static int getIdOfFood(String name) {
        return assertOnFood()
                .extract()
                .jsonPath()
                .getInt("data.findAll{ it.name == '" + name + "' }.last().id");

    }

    private ValidatableResponse assertOnRename(int id, int version, String newName, int expirationOffset, int location) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("new", newName)
                .queryParam("expirationoffset", expirationOffset)
                .queryParam("location", location).
        when()
                .put(TestSuite.DOMAIN + "/v2/food/rename").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    private ValidatableResponse assertOnEdit(int id, int version, String newName, int expirationOffset, int location, String description) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("new", newName)
                .queryParam("expirationoffset", expirationOffset)
                .queryParam("location", location)
                .formParam("description", description).
        when()
                .put(TestSuite.DOMAIN + "/v2/food/edit").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);
    }

    private ValidatableResponse assertOnSetBuyStatus(int id, int version, boolean toBuy) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("id", id)
                .queryParam("version", version)
                .queryParam("buy", toBuy ? 1 : 0).
        when()
                .put(TestSuite.DOMAIN + "/v2/food/buy").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    private static ValidatableResponse assertOnFood() {
        return assertOnFood(false);
    }

    public static ValidatableResponse assertOnFood(boolean bitemporal) {
        return
        given()
                .log().ifValidationFails()
                .queryParam("bitemporal", bitemporal ? 1 : 0).
        when()
                .get(TestSuite.DOMAIN + "/v2/food").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    @Override
    public String getEndpoint() {
        return "/v2/food";
    }

    @Inject
    void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
}
