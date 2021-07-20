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

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.data.Data;
import de.njsm.stocks.servertest.TestSuite;
import groovy.lang.Tuple2;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Cleanup {

    @Test
    public void cleanFoodItems() {
        List<Data> ids = getIds("/v2/fooditem");

        for (Data d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/fooditem").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanEan() {
        List<Data> ids = getIds("/v2/ean");

        for (Data d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/ean").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanFood() {
        List<Data> ids = getIds("/v2/food");

        for (Data d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/food").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanLocations() {
        List<Data> ids = getIds("/v2/location");

        for (Data d : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/location").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanDevices() {
        List<Data> ids = getIds("/v2/device");

        for (Data d : ids) {
            if (d.id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/device").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanUsers() {
        List<Data> ids = getIds("/v2/user");

        for (Data d : ids) {
            if (d.id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/user").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }


    @Test
    public void cleanScaledUnits() {
        List<Data> ids = getIds("/v2/scaled-unit");

        for (Data d : ids) {
            if (d.id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/scaled-unit").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanUnits() {
        List<Data> ids = getIds("/v2/unit");

        for (Data d : ids) {
            if (d.id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", d.id)
                    .queryParam("version", d.version).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/unit").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void setupOtherTestAccounts() throws IOException {
        Tuple2<Integer, String> ticket1 = DeviceTest.createNewDevice("cli-client", 1);
        Tuple2<Integer, String> ticket2 = DeviceTest.createNewDevice("android-client", 1);

        writeToFile("target/01_ticket", ticket1.getSecond());
        writeToFile("target/01_id", String.valueOf(ticket1.getFirst()));
        writeToFile("target/02_ticket", ticket2.getSecond());
        writeToFile("target/02_id", String.valueOf(ticket2.getFirst()));

    }

    private void writeToFile(String filename, String content) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(content);
        writer.close();
    }

    private List<Data> getIds(String path) {
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

        List<Data> result = new LinkedList<>();
        while (it1.hasNext()) {
            result.add(new Data(it1.next(), it2.next()));
        }
        return result;
    }
}
