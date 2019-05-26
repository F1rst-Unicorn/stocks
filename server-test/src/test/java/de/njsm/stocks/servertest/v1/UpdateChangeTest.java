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

package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertTrue;

public class UpdateChangeTest {

    @Test
    public void updatesChangeOnDataChange() {
        String olderDate = getLocationChangeDate();
        changeLocations();
        String youngerDate = getLocationChangeDate();

        assertTrue(olderDate + " is not older than " + youngerDate,
                olderDate.compareTo(youngerDate) < 0);
    }

    private void changeLocations() {
        given()
                .contentType(ContentType.JSON)
                .body(new Location(1, "Cupboard")).
        when()
                .put(TestSuite.DOMAIN + "/location").
        then()
                .statusCode(204);
        given()
                .contentType(ContentType.JSON)
                .body(new Location(1, "Cupboard")).
        when()
                .put(TestSuite.DOMAIN + "/location/remove").
        then()
                .statusCode(204);
    }

    private String getLocationChangeDate() {
        Response response =
        when()
                .get(TestSuite.DOMAIN + "/update").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", iterableWithSize(6))
                .extract()
                .response();

        return response.jsonPath().getString("findAll{ it.table == 'Location' }[0].lastUpdate");
    }
}
