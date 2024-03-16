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

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static de.njsm.stocks.servertest.v2.LocationTest.addLocationType;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(400)
public class UpdateChangeTest {

    @Test
    void updatesChangeOnDataChange() {
        String olderDate = getLocationChangeDate();
        addALocation("1");
        String youngerDate = getLocationChangeDate();

        assertTrue(olderDate.compareTo(youngerDate) < 0,
                olderDate + " is not older than " + youngerDate);

        olderDate = youngerDate;
        addALocation("2");
        youngerDate = getLocationChangeDate();

        assertTrue(olderDate.compareTo(youngerDate) < 0,
                olderDate + " is not older than " + youngerDate);
    }

    @Test
    void gettingChangesStartingFromDateWorks() {
        addALocation("3");
        String lastChangeDate = getLocationChangeDate();
        addALocation("4");

        getDataYoungerThan(lastChangeDate)
                .body("data", iterableWithSize(1));
    }

    private ValidatableResponse getDataYoungerThan(String date) {
        return given()
                .queryParam("bitemporal", 1)
                .queryParam("startingFrom", date).
        when()
                .get(TestSuite.DOMAIN + "/v2/location").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
    }

    private void addALocation(String name) {
        addLocationType("update " + name);
    }

    private String getLocationChangeDate() {
        Response response =
        when()
                .get(TestSuite.DOMAIN + "/v2/update").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", iterableWithSize(11))
                .body("data.table", hasItem("Location"))
                .extract()
                .response();

        return response.jsonPath().getString("data.findAll{ it.table == 'Location' }[0].lastUpdate");
    }
}
