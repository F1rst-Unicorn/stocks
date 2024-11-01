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

import de.njsm.stocks.client.business.Constants;
import de.njsm.stocks.client.business.RecipeDeleteService;
import de.njsm.stocks.client.business.UserDeviceAddService;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.common.api.VersionedData;
import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v2.repo.RecipeRepository;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@TestMethodOrder(MethodOrderer.MethodName.class)
@Order(1600)
public class Cleanup extends Base {

    private UserDeviceAddService userDeviceAddService;

    private RecipeRepository recipeRepository;

    private RecipeDeleteService recipeDeleteService;

    @BeforeEach
    void setUp() {
        dagger.inject(this);
    }

    @Test
    void clean01Devices() {
        List<VersionedData> ids = getIds("/v2/device");

        List<Integer> ignoredDevices = List.of(1, 2);

        for (VersionedData d : ids) {
            if (ignoredDevices.contains(d.id())) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/device").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean02Ean() {
        List<VersionedData> ids = getIds("/v2/ean");

        for (VersionedData d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/ean").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean03FoodItems() {
        List<VersionedData> ids = getIds("/v2/fooditem");

        for (VersionedData d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/fooditem").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean04Recipes() {
        List<RecipeForSynchronisation> data = recipeRepository.getAll();

        for (RecipeForSynchronisation d : data) {
            var ingredients = updateService.getRecipeIngredients(Instant.EPOCH);
            var x = ingredients
                    .stream()
                    .filter(it -> it.recipe() == d.id())
                    .filter(it -> it.transactionTimeEnd().equals(Constants.INFINITY))
                    .filter(it -> it.validTimeStart().isBefore(Instant.now()))
                    .filter(it -> it.validTimeEnd().isAfter(Instant.now()))
                    .map(it -> RecipeIngredientDeleteNetworkData.create(it.id(), it.version()))
                    .toList();
            var products = updateService.getRecipeProducts(Instant.EPOCH)
                    .stream()
                    .filter(it -> it.recipe() == d.id())
                    .filter(it -> it.transactionTimeEnd().equals(Constants.INFINITY))
                    .filter(it -> it.validTimeStart().isBefore(Instant.now()))
                    .filter(it -> it.validTimeEnd().isAfter(Instant.now()))
                    .map(it -> RecipeProductDeleteNetworkData.create(it.id(), it.version()))
                    .toList();

            recipeDeleteService.delete(RecipeDeleteData.create(
                    VersionedId.create(d.id(), d.version()),
                    x,
                    products));
        }
    }

    @Test
    void clean05Food() {
        List<VersionedData> ids = getIds("/v2/food");

        for (VersionedData d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/food").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean06Locations() {
        List<VersionedData> ids = getIds("/v2/location");

        for (VersionedData d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/location").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean07ScaledUnits() {
        List<VersionedData> ids = getIds("/v2/scaled-unit");

        for (VersionedData d : ids) {
            if (d.id() == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/scaled-unit").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean08Units() {
        List<VersionedData> ids = getIds("/v2/unit");

        for (VersionedData d : ids) {
            if (d.id() == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/unit").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void clean09Users() {
        List<VersionedData> ids = getIds("/v2/user");

        List<Integer> ignoredUsers = List.of(1, 2);

        for (VersionedData d : ids) {
            if (ignoredUsers.contains(d.id())) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id())
                    .queryParam("version", d.version()).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/user").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    void setupOtherTestAccounts() throws IOException {
        NewClientTicket ticket1 = userDeviceAddService.add(UserDeviceAddForm.create("cli-client", IdImpl.create(1)));
        NewClientTicket ticket2 = userDeviceAddService.add(UserDeviceAddForm.create("android-client", IdImpl.create(1)));

        writeToFile("build/01_ticket", ticket1.ticket());
        writeToFile("build/01_id", String.valueOf(ticket1.id().id()));
        writeToFile("build/02_ticket", ticket2.ticket());
        writeToFile("build/02_id", String.valueOf(ticket2.id().id()));

    }

    private void writeToFile(String filename, String content) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(content);
        writer.close();
    }

    private List<VersionedData> getIds(String path) {
        JsonPath jsonPath = when()
                .get(TestSuite.DOMAIN + path).
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath();

        List<Integer> ids = jsonPath.getList("data.id", Integer.class);
        List<Integer> versions = jsonPath.getList("data.version", Integer.class);

        Iterator<Integer> it1 = ids.iterator();
        Iterator<Integer> it2 = versions.iterator();

        List<VersionedData> result = new LinkedList<>();
        while (it1.hasNext()) {
            result.add(new VersionedData(it1.next(), it2.next()));
        }
        return result;
    }

    @Inject
    void setUserDeviceAddService(UserDeviceAddService userDeviceAddService) {
        this.userDeviceAddService = userDeviceAddService;
    }

    @Inject
    void setRecipeRepository(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Inject
    void setRecipeDeleteService(RecipeDeleteService recipeDeleteService) {
        this.recipeDeleteService = recipeDeleteService;
    }
}
