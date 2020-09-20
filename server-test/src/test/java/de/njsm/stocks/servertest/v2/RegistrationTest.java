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

import de.njsm.stocks.servertest.TestSuite;
import groovy.lang.Tuple2;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyStore;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistrationTest {

    private static String ticket;

    private static int userId;

    private static int deviceId;

    private static KeyPair keypair;

    private static String commonName;

    private static KeyStore keystore;

    @BeforeClass
    public static void setupCredentials() throws Exception {
        userId = UserTest.createNewUser("Jon");
        Tuple2<Integer, String> ticket = DeviceTest.createNewDevice("Laptop", userId);
        RegistrationTest.ticket = ticket.getSecond();
        RegistrationTest.deviceId = ticket.getFirst();

        keypair = SetupTest.generateKeyPair();
        commonName = "Jon$" + userId + "$Laptop$" + deviceId;
    }

    @AfterClass
    public static void removeKeystore() {
        new File("keystore_2").deleteOnExit();
    }

    @Test
    public void test1cannotRegisterWithWrongTicket() throws Exception {
        tryFailingRegistration(deviceId, "0000", commonName);
        tryFailingRegistration(deviceId, "fdsa", commonName);
    }

    @Test
    public void test1cannotRegisterWithWrongDeviceId() throws Exception {
        tryFailingRegistration(1, ticket, commonName);
    }

    @Test
    public void test1cannotRegisterWithWrongCommonName() throws Exception {
        tryFailingRegistration(deviceId, ticket, "Jon$" + userId + "$Laptop$0");
        tryFailingRegistration(deviceId, ticket, "Jon$" + userId + "$Lapto$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "Jack$" + userId + "$Laptop$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "Jon$0$Laptop$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "");
    }

    @Test
    public void test2haveCorrectRegistration() throws Exception {
        String cert = registerSuccessfully(deviceId, ticket, commonName);
        keystore = SetupTest.getFirstKeystore();
        SetupTest.storeToDisk(keystore, cert, "keystore_2", keypair);

        accessServerWithSecondAccount(keystore)
                .statusCode(200)
                .contentType(ContentType.JSON);

        revokedUserCannotAccessAnyMore();
    }

    private void revokedUserCannotAccessAnyMore() throws InterruptedException {
        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .queryParam("id", deviceId)
                .queryParam("version", 0).
        when()
                .delete(TestSuite.DOMAIN + "/v2/device").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));
        Thread.sleep(3000);

        accessServerWithSecondAccount(keystore)
                .statusCode(400);
    }

    public static ValidatableResponse accessServerWithSecondAccount(KeyStore keystore) {
        return given()
                .log().ifValidationFails()
                .config(RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig()
                        .allowAllHostnames()
                        .trustStore(keystore)
                        .keyStore("keystore_2", SetupTest.PASSWORD))).
        when()
                .get(TestSuite.DOMAIN + "/v2/location").
        then()
                .log().ifValidationFails();
    }

    private void tryFailingRegistration(int deviceId, String ticket, String commonName) throws Exception {
        register(deviceId, ticket, commonName)
                .body("status", equalTo(6))
                .body("data", isEmptyOrNullString())
                .statusCode(401);
    }

    private String registerSuccessfully(int deviceId, String ticket, String commonName) throws Exception {
        return register(deviceId, ticket, commonName)
                .statusCode(200)
                .body("status", equalTo(0))
                .body("data", not(isEmptyOrNullString()))
                .extract()
                .jsonPath()
                .getString("data");
    }

    private ValidatableResponse register(int deviceId, String ticket, String commonName) throws Exception {
        String csr = SetupTest.getCsr(keypair, commonName);
        return
        given()
                .log().ifValidationFails()
                .formParam("device", deviceId)
                .formParam("token", ticket)
                .formParam("csr", csr).
        when()
                .post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/v2/auth/newuser").
        then()
                .log().ifValidationFails()
                .contentType(ContentType.JSON);

    }
}
