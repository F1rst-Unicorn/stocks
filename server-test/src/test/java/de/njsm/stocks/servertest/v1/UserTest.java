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

import de.njsm.stocks.common.data.User;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class UserTest {

    @Test
    public void addAUser() {
        String name = "John";

        addUser(name);

        assertOnUsers()
                .body("name", hasItems(name));
    }

    @Test
    public void removeAUser() {
        String name = "July";
        addUser(name);
        int id = getIdOfUser(name);

        given()
                .contentType(ContentType.JSON)
                .body(new User(id, "")).
        when()
                .put(TestSuite.DOMAIN + "/user/remove").
        then()
                .log().ifValidationFails()
                .statusCode(204);

        assertOnUsers()
                .body("name", not(hasItems(name)));
    }

    public static int createNewUser(String name) {
        addUser(name);
        return getIdOfUser(name);
    }

    private static int getIdOfUser(String firstName) {
        return when()
                    .get(TestSuite.DOMAIN + "/user").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getInt("findAll{ it.name == '" + firstName + "' }.id[0]");
    }

    private static ValidatableResponse assertOnUsers() {
        return
                when()
                        .get(TestSuite.DOMAIN + "/user").
                then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON);
    }

    private static void addUser(String firstName) {
        User data = new User(0, firstName);
        given()
                .contentType(ContentType.JSON)
                .body(data).
        when()
                .put(TestSuite.DOMAIN + "/user").
        then()
                .log().ifValidationFails()
                .statusCode(204);
    }
}
